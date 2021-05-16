package masil.example.springdata.jdbc.identity;

import lombok.Getter;
import lombok.Value;
import lombok.With;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.domain.Persistable;
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

@SpringJUnitConfig(value = MappingCompositeIDToPKTest.Config.class)
public class MappingCompositeIDToPKTest {


    @Configuration
    public static class Config extends AbstractJdbcConfiguration {
        @Bean
        DataSource dataSource() {
            return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).addScript("MappingCompositeIDToPKTest.sql").build();
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
            return new JdbcCustomConversions(Arrays.asList(StringToTestEntityId.INSTANCE, TestEntityIdToString.INSTANCE));
        }
    }

    @Autowired
    JdbcAggregateOperations jdbcAggregateOperations;

    @Getter
    @Table("TEST_TABLE1")
    public static class TestEntity implements Persistable<TestEntityId> {

        public static TestEntity of(TestEntityId id, String name) {
            return new TestEntity(id, name, true);
        }

        @Id
        final TestEntityId id;

        final String name;

        @With
        @Transient
        final Boolean isNew;

        @PersistenceConstructor
        private  TestEntity(TestEntityId id, String name) {
            this(id, name, false); //for read from db
        }

        private TestEntity(TestEntityId id, String name, Boolean isNew) {
            this.id = id;
            this.name = name;
            this.isNew = isNew;
        }

        @Override
        public boolean isNew() {
            return isNew;
        }
    }


    @Value(staticConstructor = "of")
    public static class TestEntityId {
        Long key1;
        String key2;
    }

    @ReadingConverter
    enum StringToTestEntityId implements Converter<String, TestEntityId> {
        INSTANCE;
        @Override
        public TestEntityId convert(String source) {
            String[] split = source.split("::");
            return TestEntityId.of(Long.parseLong(split[0]), split[1]);
        }
    }

    @WritingConverter
    enum TestEntityIdToString implements Converter<TestEntityId, String>{
        INSTANCE;
        @Override
        public String convert(TestEntityId source) {
            return source.key1+"::"+source.getKey2();
        }
    }

    @Test
    @DisplayName("Mapping Composite Identity to a PK")
    void map_to_pk() {
        TestEntity entity = TestEntity.of(TestEntityId.of(1L,"Wansu"), "foo");
        jdbcAggregateOperations.save(entity);

        assert entity.getId() != null;

        TestEntity find = jdbcAggregateOperations.findById(entity.getId(), TestEntity.class);

    }
}
