package masil.example.springdata.jdbc.persisting_entities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import masil.example.springdata.jdbc.AbstractBaseJdbcTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Object Mapping")
@SpringJUnitConfig(classes = ObjectCreationTest.Config.class)
public class ObjectCreationTest {

    public static class Config extends AbstractBaseJdbcTestConfig {
        @Override
        protected String getScript() {
            return "schema.sql";
        }
    }

    @Autowired
    JdbcAggregateOperations operations;

    @Table("PRODUCT")
    @Getter
    public static class TestEntity {
        @Id
        private Long id;
        private String name;

        @Transient
        private boolean defaultConstructCall = false;

        private TestEntity() {
            defaultConstructCall = true;
        }

        public TestEntity(String name) {
            this.name = name;
        }

    }

    @Test
    @DisplayName("If there’s a no-argument constructor, it will be used. Other constructors will be ignored.")
    void object_creation_resolution_algorithm_1() {
        TestEntity saved = operations.save(new TestEntity("macbook"));
        TestEntity found = operations.findById(saved.getId(), TestEntity.class);

        assert found != null;

        assertThat(found.isDefaultConstructCall()).isTrue();
        assertThat(found.getName()).isEqualTo("macbook");
    }

    @Getter
    @Table("PRODUCT")
    public static class TestObject2 {
        @Id
        private Long id;
        private String name;

        @Transient
        private boolean constructCall = false;

        public TestObject2(String name) {
            this.name = name;
            this.constructCall = true;
        }
    }

    @Test
    @DisplayName("If there’s a single constructor taking arguments, it will be used.")
    void object_creation_resolution_algorithm_2() {
        TestObject2 saved = operations.save(new TestObject2("macbook"));
        TestObject2 found = operations.findById(saved.getId(), TestObject2.class);

        assert found != null;

        assertThat(found.isConstructCall()).isTrue();
    }

    @Getter
    @Table("PRODUCT")
    @AllArgsConstructor
    public static class TestObject3 {
        @Id
        private Long id;
        private String name;

        public TestObject3(String name) {
            this(null,name);
        }
    }

    @Getter
    @Table("PRODUCT")
    public static class TestObject4 {
        @Id
        private Long id;
        private String name;

        @Transient
        private boolean constructCall = false;

        @PersistenceConstructor
        public TestObject4(Long id, String name) {
            this.id = id;
            this.name = name;
            this.constructCall = true;
        }

        public TestObject4(String name) {
            this(null,name);
        }
    }

    @Test
    @DisplayName("If there are multiple constructors taking arguments, the one to be used by Spring Data will have to be annotated with @PersistenceConstructor.")
    void object_creation_resolution_algorithm_3() {
        TestObject3 saved = operations.save(new TestObject3 ("macbook"));

        assertThatThrownBy(() -> operations.findById(saved.getId(), TestObject3 .class))
        .hasMessageContaining("using constructor NO_CONSTRUCTOR with arguments");

        TestObject4 saved2 = operations.save(new TestObject4 ("macbook"));
        TestObject4 found = operations.findById(saved2.getId(), TestObject4.class);

        assert found != null;

        assertThat(found.isConstructCall()).isTrue();
    }

    @Getter
    @Table("PRODUCT")
    // ** Recommend!!!!
    @AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_=@PersistenceConstructor )
    public static class TestObject5 {

        public static TestObject5 of(String name) {
            return new TestObject5(name);
        }

        @Id
        private Long id;
        private String name;

        TestObject5(String name) {
            this(null, name );
        }
    }
}
