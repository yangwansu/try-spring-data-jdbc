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
import org.springframework.data.annotation.Transient;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
public class MappingIDToPkTest {

    @Configuration
    public static class Config extends AbstractBaseJdbcTestConfig {
        @Override
        protected String getScript() {
            return "MappingIDToPkTest.sql";
        }

        @Override
        protected List<Object> getConverters() {
            return Arrays.asList(new LongToTestEntityIdConvertor(), new TestEntityIdToLongConvertor());
        }
    }

    @Autowired
    JdbcAggregateOperations aggregateOperations;

    @Getter
    @Table("TEST_TABLE")
    @AllArgsConstructor(access = PRIVATE, staticName = "of") //in new
    @RequiredArgsConstructor(access = PRIVATE, onConstructor_=@PersistenceConstructor) // in select isNew is false
    public static class TestEntity implements Persistable<TestEntityId> {

        public static TestEntity of(TestEntityId id, String name) {
            boolean isNew = true;
            return new TestEntity(id, name, isNew);
        }

        @Id
        private final TestEntityId id;
        private final String name;

        @Transient
        private boolean isNew = false;

        @Override
        public boolean isNew() {
            return isNew;
        }
    }

    @Value(staticConstructor = "of")
    public static class TestEntityId {
        Long id;
    }


    @ReadingConverter
    public static class LongToTestEntityIdConvertor implements Converter<Long, TestEntityId> {
        @Override
        public TestEntityId convert(Long source) {
            return TestEntityId.of(source);
        }
    }

    @WritingConverter
    public static class TestEntityIdToLongConvertor implements Converter<TestEntityId,Long> {
        @Override
        public Long convert(TestEntityId source) {
            return source.getId();
        }
    }

    @Test
    @DisplayName("")
    void name() {
        TestEntity entity = TestEntity.of(TestEntityId.of(1L), "foo");
        assertThat(entity.isNew()).isTrue();
        aggregateOperations.save(entity);
        //aggregateOperations.save(entity);

        TestEntity find = aggregateOperations.findById(entity.getId(), TestEntity.class);
        assertThat(find.isNew()).isFalse();
    }
}

