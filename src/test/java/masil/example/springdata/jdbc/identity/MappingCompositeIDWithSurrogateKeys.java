package masil.example.springdata.jdbc.identity;

import lombok.*;
import masil.example.springdata.jdbc.AbstractBaseJdbcTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@SpringJUnitConfig(value = MappingCompositeIDWithSurrogateKeys.Config.class)
public class MappingCompositeIDWithSurrogateKeys {


    @Configuration
    @EnableJdbcRepositories
    public static class Config extends AbstractBaseJdbcTestConfig {

        @Override
        protected String getScript() {
            return "MappingCompositeIDWithSurrogateKeysTest.sql";
        }
    }

    @Autowired
    TestEntityRepository repository;

    interface TestEntityRepository extends CrudRepository<TestEntity, TestEntityId> {

        Optional<TestEntity> findByEntityId(TestEntityId id);
    }


    @Table("TEST_TABLE")
    @AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_=@PersistenceConstructor)
    @RequiredArgsConstructor(staticName = "of")
    public static class TestEntity {
        @Id
        private Long id;

        @Getter
        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
        private final TestEntityId entityId;

        public TestEntityId getId() {
            return entityId;
        }
    }

    @Value(staticConstructor = "of")
    public static class TestEntityId {
        Long key1;
        String key2;
    }


    @Test
    void name() {
        TestEntity entity = TestEntity.of(TestEntityId.of(1L, "abc"));
        repository.save(entity);

        TestEntityId id = entity.getId();

        Optional<TestEntity> find = repository.findByEntityId(id);

        System.out.println(find);

    }
}

