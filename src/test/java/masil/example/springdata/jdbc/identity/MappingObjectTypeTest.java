package masil.example.springdata.jdbc.identity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import masil.example.springdata.jdbc.AbstractBaseJdbcTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = MappingObjectTypeTest.Config.class)
public class MappingObjectTypeTest {

    @Configuration
    public static class Config extends AbstractBaseJdbcTestConfig {
        @Override
        protected String getScript() {
            return "MappingObjectTypeTest.sql";
        }

        @Override
        protected List<Object> getConverters() {
            return Arrays.asList(new IntegerToTestEntityIdConvertor(), new TestEntityIdToIntegerConvertor());
        }
    }

    @Autowired
    JdbcAggregateOperations aggregateOperations;

    @Getter
    @Table("TEST_TABLE")
    @AllArgsConstructor(access = PRIVATE, onConstructor_=@PersistenceConstructor)
    @RequiredArgsConstructor(staticName = "of")
    public static class TestEntity {
        @Id
        private TestEntityId id;

        private final String name;
    }

    @Value(staticConstructor = "of")
    public static class TestEntityId {
        OtherAggregationRef value;
    }
    @Value(staticConstructor = "of")
    public static class OtherAggregationRef {
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
    @DisplayName("Object Type ID Mapping ")
    void objectTypeId() {

        TestEntity saved = aggregateOperations.save(TestEntity.of("xxx"));

        TestEntity find = aggregateOperations.findById(saved.getId(), TestEntity.class);

        assert find != null;

        assertThat(find.getId()).isEqualTo(saved.getId());

    }
}
