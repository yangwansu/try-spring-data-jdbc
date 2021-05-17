package masil.example.springdata.jdbc.identity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import masil.example.springdata.jdbc.AbstractBaseJdbcTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(MappingCompositeIDToCompositeKeysTest.Config.class)
public class MappingCompositeIDToCompositeKeysTest {

    @Configuration
    public static class Config extends AbstractBaseJdbcTestConfig {
        @Override
        protected String getScript() {
            return "MappingCompositeIDToCompositeKeysTest.sql";
        }
    }

    @Autowired
    JdbcAggregateOperations jdbcAggregateOperations;


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
