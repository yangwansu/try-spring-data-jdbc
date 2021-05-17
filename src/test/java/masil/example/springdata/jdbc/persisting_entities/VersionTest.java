package masil.example.springdata.jdbc.persisting_entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import masil.example.springdata.jdbc.AbstractBaseJdbcTestConfig;
import org.assertj.core.api.AbstractLongAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@SpringJUnitConfig
public class VersionTest {

    public static class Config extends AbstractBaseJdbcTestConfig {
        @Override
        protected String getScript() {
            return "VersionTest.sql";
        }
    }

    @Autowired
    TestEntityRepository repository;

    interface TestEntityRepository extends CrudRepository<TestEntity, Long> { }

    /**
     * Property inspection
     * <p>
     * If a property annotated with @Version is present and null, or in case of a version property of primitive type 0 the entity is considered new.
     * If the version property is present but has a different value, the entity is considered to not be new.
     * If no version property is present Spring Data falls back to inspection of the Id-Property.
     * <p>
     * <p>
     * Spring Data JDBC supports optimistic locking by means of a numeric attribute that is annotated with @Version on the aggregate root.
     * Whenever Spring Data JDBC saves an aggregate with such a version attribute two things happen:
     * The update statement for the aggregate root will contain a where clause checking that the version stored in the database is actually unchanged.
     * If this isn’t the case an OptimisticLockingFailureException will be thrown.
     * Also the version attribute gets increased both in the entity and in the database so a concurrent action will notice the change and throw an OptimisticLockingFailureException if applicable as described above.
     * This process also applies to inserting new aggregates, where a null or 0 version indicates a new instance and the increased instance afterwards marks the instance as not new anymore, making this work rather nicely with cases where the id is generated during object construction for example when UUIDs are used.
     * During deletes the version check also applies but no version is increased.
     */
    @Getter
    @Table("TEST_TABLE")
    @AllArgsConstructor(access = PRIVATE, onConstructor_ = @PersistenceConstructor)
    public static class TestEntity {

        public static TestEntity newEntity(Long id) {
            return new TestEntity(id, null);
        }

        //for test
        public TestEntity rollbackVersion() {
            return new TestEntity(id, version - 1);
        }

        @Id
        private final Long id;
        @Version
        private final Long version;
    }

    @Test
    void update_version() {

        TestEntity newEntity = TestEntity.newEntity(999L);
        assertThatVersionOf(newEntity).isEqualTo(null);

        TestEntity saved = repository.save(newEntity);  //INSERT version 0L
        assertThatVersionOf(saved).isEqualTo(0L);

        Optional<TestEntity> find = repository.findById(saved.getId());
        assertThatVersionOf(find).isEqualTo(0L);

        saved = repository.save(find.get());    //UPDATE version 1L where version = 0L
        assertThatVersionOf(saved).isEqualTo(1L);

        find = repository.findById(saved.getId());
        assertThatVersionOf(find).isEqualTo(1L);

        saved = repository.save(find.get());    //UPDATE version 2L where version = 1L
        assertThatVersionOf(saved).isEqualTo(2L);
    }


    @Test
    void failOptimisticLocking() {
        TestEntity newEntity = TestEntity.newEntity(999L);

        TestEntity saved = repository.save(newEntity);  //INSERT version 0L
        saved = repository.save(saved);  //UPDATE version 1L where version 0L
        assertThatVersionOf(saved).isEqualTo(1L);

        TestEntity rollback = saved.rollbackVersion();
        assertThatVersionOf(rollback).isEqualTo(0L);

        assertThatThrownBy(() -> repository.save(rollback))
                .hasRootCauseInstanceOf(OptimisticLockingFailureException.class)
                .hasRootCauseMessage("Optimistic lock exception on saving entity of type %s.", TestEntity.class.getName());

    }

    public static AbstractLongAssert<?> assertThatVersionOf(TestEntity entity) {
        return assertThat(entity.getVersion());
    }

    public static AbstractLongAssert<?> assertThatVersionOf(Optional<TestEntity> entity) {
        return assertThatVersionOf(entity.get());
    }
}
