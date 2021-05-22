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

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@SpringJUnitConfig
public class OptimisticLockingTest {
    public static class Config extends AbstractBaseJdbcTestConfig {
        @Override
        protected String[] getSql() {
            return new String[]{
                    "CREATE TABLE IF NOT EXISTS TEST_TABLE (id bigint primary key, version bigint)"
            };
        }
    }
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

    @Autowired
    TestEntityRepository repository;

    interface TestEntityRepository extends CrudRepository<TestEntity, Long> { }

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


}
