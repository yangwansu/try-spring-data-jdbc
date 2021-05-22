package masil.example.springdata.jdbc.persisting_entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import masil.example.springdata.jdbc.AbstractBaseJdbcTestConfig;
import org.assertj.core.api.AbstractLongAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@SpringJUnitConfig
public class VersionTest {

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

    @Autowired
    TestEntityRepository repository;

    interface TestEntityRepository extends CrudRepository<TestEntity, Long> { }

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
