package masil.example.springdata.jdbc;

import lombok.SneakyThrows;
import org.junit.platform.commons.util.StringUtils;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;

@Configuration
@EnableJdbcRepositories(considerNestedRepositories = true)
public abstract class AbstractBaseJdbcTestConfig extends AbstractJdbcConfiguration {

    protected String[] getSql() {
        return new String[]{};
    }

    protected String getScript() {
        return null;
    }

    protected List<Object> getConverters() {
        return Arrays.asList();
    }

    @Bean
    @SneakyThrows
    DataSource dataSource() {
        String str = Arrays.stream(getSql()).reduce(new BinaryOperator<String>() {
            @Override
            public String apply(String s, String s2) {
                return s+"\n"+s2;
            }
        }).orElse("");

        File file = createTempFile();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));) {
            writer.write(str);
        } catch (IOException e) {
            throw e;
        }

        try {
            EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL);
            if (StringUtils.isNotBlank(getScript())) {
                builder.addScript(getScript());
            }
            if (file.exists() && StringUtils.isNotBlank(str)) {
                builder = builder.addScript("file:" + file.getAbsolutePath());
            }

            return builder.build();
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }

    }

    private File createTempFile() throws IOException {
        String tmpdir = System.getProperty("java.io.tmpdir");
        Path path = Paths.get(tmpdir);
        // Create an temporary file in a specified directory.
        Path temp = Files.createTempFile(path, null, ".sql");
        return temp.toFile();
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
