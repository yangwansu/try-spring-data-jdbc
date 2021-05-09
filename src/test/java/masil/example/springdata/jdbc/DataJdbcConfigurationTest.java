package masil.example.springdata.jdbc;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.dialect.HsqlDbDialect;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;


@SpringJUnitConfig(classes = DataJdbcConfiguration.class)
class DataJdbcConfigurationTest {


    @Autowired
    ApplicationContext ctx;

    @Autowired
    ProductRepository repository;

    @Autowired
    Dialect dialect;

    @Test
    void beans() {
        Assertions.assertThat(dialect).isInstanceOf(HsqlDbDialect.class);
    }
}