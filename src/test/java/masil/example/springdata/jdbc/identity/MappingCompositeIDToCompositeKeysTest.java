package masil.example.springdata.jdbc.identity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import masil.example.springdata.jdbc.AbstractBaseJdbcTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@SpringJUnitConfig
public class MappingCompositeIDToCompositeKeysTest {

    @Configuration
    public static class Config extends AbstractBaseJdbcTestConfig {
        @Override
        protected String[] getSql() {
            return new String[]{
                    "CREATE TABLE IF NOT EXISTS TEST_TABLE (id BIGINT, name varchar(100), PRIMARY KEY(id, name))"
            };
        }
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
