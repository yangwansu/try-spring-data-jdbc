package masil.example.springdata.jdbc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableJdbcRepositories(considerNestedRepositories = true)
public abstract class AbstractBaseJdbcTestConfig extends AbstractJdbcConfiguration {


    protected abstract String getScript();

    protected List<Object> getConverters() {
        return Arrays.asList();
    }

    @Bean
    DataSource dataSource() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).addScript(getScript()).build();
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
        List<Object> converters = getConverters();
        return new JdbcCustomConversions(converters);
    }


}
