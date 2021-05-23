package masil.example.springdata.jdbc.identity;

import lombok.Getter;
import lombok.Value;
import lombok.With;
import masil.example.springdata.jdbc.AbstractBaseJdbcTestConfig;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@SpringJUnitConfig(classes = MappingCompositeIDToPKTest.class)
public class MappingCompositeIDToPKTest extends AbstractBaseJdbcTestConfig {

    @Override
    protected String[] getSql() {
        return new String[]{
                "CREATE TABLE IF NOT EXISTS TEST_TABLE1 (id varchar(100) primary key, name varchar(100))"
        };
    }

    @Override
    protected List<Object> getConverters() {
        return Arrays.asList(StringToTestEntityId.INSTANCE, TestEntityIdToString.INSTANCE);
    }

    @Autowired
    TestEntityRepository repository;

    interface TestEntityRepository extends CrudRepository<TestEntity, TestEntityId> {
    }

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
        private TestEntity(TestEntityId id, String name) {
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
    enum TestEntityIdToString implements Converter<TestEntityId, String> {
        INSTANCE;

        @Override
        public String convert(TestEntityId source) {
            return source.key1 + "::" + source.getKey2();
        }
    }

    @Test
    @DisplayName("Mapping Composite Identity to a PK")
    void map_to_pk() {
        TestEntity entity = TestEntity.of(TestEntityId.of(1L, "Wansu"), "foo");
        repository.save(entity);

        assert entity.getId() != null;

        Optional<TestEntity> find = repository.findById(entity.getId());
        assertThat(find).isPresent();

    }
}
