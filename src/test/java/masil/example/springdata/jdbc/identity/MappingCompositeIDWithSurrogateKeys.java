package masil.example.springdata.jdbc.identity;

import lombok.*;
import masil.example.springdata.jdbc.DataJdbcTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@SpringJUnitConfig(classes = MappingCompositeIDWithSurrogateKeys.class)
public class MappingCompositeIDWithSurrogateKeys extends DataJdbcTestSupport {
    @Override
    protected String[] getSql() {
        return new String[] {
                "CREATE TABLE IF NOT EXISTS TEST_TABLE (id integer primary key identity, key1 bigint, key2  varchar(100), UNIQUE (key1, key2))"
        };
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

