package masil.example.springdata.jdbc.persisting_entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import masil.example.springdata.jdbc.AbstractBaseJdbcTestConfig;
import org.assertj.core.api.AbstractLongAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@SpringJUnitConfig
public class VersionPropertiesInspectionTest {

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

        @Id
        private final Long id;
        @Version
        private final Long version;
    }

    interface TestEntityRepository extends CrudRepository<TestEntity, Long> { }

    @Autowired
    TestEntityRepository repository;
    @Autowired
    JdbcMappingContext context;

    RelationalPersistentEntity<?> persistentEntity;

    /**
     * @see JdbcAggregateTemplate#save(java.lang.Object)
     *
     */
    @BeforeEach
    void setUp() {
        persistentEntity = context.getRequiredPersistentEntity(TestEntity.class);
    }

    @Test
    @DisplayName("Detect whether an entity is new")
    void detect_whether_an_entity_is_new() {
        TestEntity newEntity = TestEntity.newEntity(999L);

        assertThat(newEntity.getId()).isNotNull();
        assertThat(newEntity.getVersion()).isNull();
        assertThat(persistentEntity.isNew(newEntity)).isTrue();

        TestEntity saved = repository.save(newEntity);
        assertThat(saved.getVersion()).isEqualTo(0L); //version null -> 0
        assertThat(persistentEntity.isNew(saved)).isFalse();
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


    public static AbstractLongAssert<?> assertThatVersionOf(TestEntity entity) {
        return assertThat(entity.getVersion());
    }

    public static AbstractLongAssert<?> assertThatVersionOf(Optional<TestEntity> entity) {
        return assertThatVersionOf(entity.get());
    }
}
