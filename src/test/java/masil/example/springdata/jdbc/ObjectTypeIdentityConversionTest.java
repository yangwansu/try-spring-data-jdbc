package masil.example.springdata.jdbc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.relational.core.mapping.Column;
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

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = ObjectTypeIdentityConversionTest.Config.class)
public class ObjectTypeIdentityConversionTest {

    @Configuration
    public static class Config extends AbstractJdbcConfiguration {

        @Bean
        DataSource dataSource() {
            return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).addScript("ObjectTypeIdentityConversionTest.sql").build();
        }
        @Bean
        NamedParameterJdbcOperations jdbcOperations(DataSource dataSource) {
            return new NamedParameterJdbcTemplate(dataSource);
        }
        @Bean
        TransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        @Override
        public JdbcCustomConversions jdbcCustomConversions() {
            return new JdbcCustomConversions(Arrays.asList(new IntegerToTestEntityIdConvertor(), new TestEntityIdToIntegerConvertor()));
        }

    }

    @Autowired
    JdbcAggregateOperations aggregateOperations;

    @Getter
    @Table
    @AllArgsConstructor(access = PRIVATE, onConstructor_=@PersistenceConstructor)
    @RequiredArgsConstructor(staticName = "of")
    public static class TestEntity {
        @Id
        private TestEntityId id;

        private final String name;
    }

    @Value(staticConstructor = "of")
    public static class TestEntityId {
        @Column("ID")
        OtherAggregationRef value;
    }
    @Value(staticConstructor = "of")
    public static class OtherAggregationRef {
        @Column("ID")
        Integer id;
    }

    @ReadingConverter
    public static class IntegerToTestEntityIdConvertor implements Converter<Integer, TestEntityId> {

        @Override
        public TestEntityId convert(Integer source) {
            return TestEntityId.of(OtherAggregationRef.of(source));
        }
    }

    @WritingConverter
    public static class TestEntityIdToIntegerConvertor implements Converter<TestEntityId,Integer> {

        @Override
        public Integer convert(TestEntityId source) {
            return source.getValue().getId();
        }
    }


    @Test
    @DisplayName("In Object Type Identity, It uses the Custom Convertor")
    void usingConvert() {

        TestEntity saved = aggregateOperations.save(TestEntity.of("xxx"));

        TestEntity find = aggregateOperations.findById(saved.getId(), TestEntity.class);

        assert find != null;

        assertThat(find.getId()).isEqualTo(saved.getId());

    }
}
