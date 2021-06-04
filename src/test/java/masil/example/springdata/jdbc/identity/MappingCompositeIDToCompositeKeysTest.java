package masil.example.springdata.jdbc.identity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import masil.example.springdata.jdbc.DataJdbcTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringJUnitConfig(classes = MappingCompositeIDToCompositeKeysTest.class)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class MappingCompositeIDToCompositeKeysTest extends DataJdbcTestSupport {
    @Override
    protected String[] getSql() {
        return new String[]{
                "CREATE TABLE IF NOT EXISTS TEST_TABLE (id BIGINT, name varchar(100), PRIMARY KEY(id, name))"
        };
    }

    @Autowired
    TestEntityRepository repository;

    interface TestEntityRepository extends CrudRepository<TestEntity, Long> { }

    @Getter
    @Table("TEST_TABLE")
    @AllArgsConstructor(staticName = "of")
    public static class TestEntity {
        private final TestEntityId id;
    }

    @Value(staticConstructor = "of")
    public static class TestEntityId {
        Long id;
        String name;
    }

    @Test
    void save() {

        TestEntity entity = TestEntity.of(TestEntityId.of(1L, "wansu"));

        // give up
        //Right now all SQL statements assume a simple value for the id column. And I don't think there is a workaround for that.
        //https://github.com/spring-projects/spring-data-jdbc/issues/574
    }
}
