package masil.example.springdata.jdbc.identity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;
import java.util.Arrays;

@SpringJUnitConfig(MappingCompositeIDToCompositeKeysTest.Config.class)
public class MappingCompositeIDToCompositeKeysTest {

    @Configuration
    public static class Config extends AbstractJdbcConfiguration {

        @Bean
        DataSource dataSource() {
            return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).addScript("MappingCompositeIDToCompositeKeysTest.sql").build();
        }
        @Bean
        NamedParameterJdbcOperations jdbcOperations(DataSource dataSource) {
            return new NamedParameterJdbcTemplate(dataSource);
        }
        @Bean
        TransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        @Override
        public JdbcCustomConversions jdbcCustomConversions() {
            return new JdbcCustomConversions(Arrays.asList());
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
