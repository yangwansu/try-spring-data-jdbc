package masil.example.springdata.jdbc.identity;

import lombok.*;
import masil.example.springdata.jdbc.AbstractBaseJdbcTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

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
    JdbcAggregateOperations aggregateOperations;



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



    @Autowired
    TestEntityRepository repository;

    @Test
    void name() {
        TestEntity entity = TestEntity.of(TestEntityId.of(1L, "abc"));
        aggregateOperations.save(entity);

        TestEntityId id = entity.getId();

        Optional<TestEntity> find = repository.findByEntityId(id);

        System.out.println(find);



    }
}

interface TestEntityRepository extends CrudRepository<MappingCompositeIDWithSurrogateKeys.TestEntity, Long> {

    Optional<MappingCompositeIDWithSurrogateKeys.TestEntity> findByEntityId(MappingCompositeIDWithSurrogateKeys.TestEntityId id);
}
