package masil.example.springdata.jdbc.identity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import masil.example.springdata.jdbc.DataJdbcTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@SpringJUnitConfig(classes = MappingIDToPkTest.class)
public class MappingIDToPkTest extends DataJdbcTestSupport {

    @Override
    protected String[] getSql() {
        return new String[] {
                "CREATE TABLE IF NOT EXISTS TEST_TABLE (id BIGINT PRIMARY KEY, name varchar(100))"
        };
    }

    @Override
    protected List<Object> getConverters() {
        return Arrays.asList(new LongToTestEntityIdConvertor(), new TestEntityIdToLongConvertor());
    }

    @Autowired
    TestEntityRepository repository;

    interface TestEntityRepository extends CrudRepository<TestEntity, TestEntityId> { }

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
        repository.save(entity);
        //repository.save(entity);

        Optional<TestEntity> find = repository.findById(entity.getId());
        assertThat(find).get().hasFieldOrPropertyWithValue("isNew", false);
    }
}

