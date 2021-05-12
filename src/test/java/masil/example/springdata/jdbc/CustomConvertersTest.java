package masil.example.springdata.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;

@SpringJUnitConfig(classes = DataJdbcConfiguration.class)
public class CustomConvertersTest {


    @Primary
    @Bean
    JdbcCustomConversions jdbcCustomConversions() {
        List<?> convertors = new ArrayList<>();
        return new JdbcCustomConversions(convertors);
    }

    @Autowired
    JdbcCustomConversions jdbcCustomConversions;

    @Test
    void name() {
//        jdbcCustomConversions.registerConvertersIn();
    }
}
