package masil.example.springdata.jdbc.ch9_6_2;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import masil.example.springdata.jdbc.DataJdbcTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@SpringJUnitConfig(classes = OneToOneTest.class)
public class OneToOneTest extends DataJdbcTestSupport {
    @Override
    protected String[] getSql() {
        return new String[]{
                "CREATE TABLE IF NOT EXISTS product (id INTEGER IDENTITY PRIMARY KEY , name varchar(100), price BIGINT, createdAt bigint)",
                "CREATE TABLE IF NOT EXISTS category (PRODUCT_ID INTEGER  , name varchar(100))"
        };
    }

    @Autowired
    TestEntityRepository repository;

    interface TestEntityRepository extends CrudRepository<TestEntity, Long> { }

    @Getter
    @Table("PRODUCT")
    @AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_=@PersistenceConstructor)
    public static class TestEntity {

        public static TestEntity of(String name, Category category) {
            return new TestEntity(name, category);
        }

        @Id
        private final Long id;
        private final String name;

        @Column("PRODUCT_ID")
        private final Category category;

        private TestEntity(String name, Category category) {
            this(null, name, category);
        }
    }


    @Value(staticConstructor = "of")
    @Table("CATEGORY")
    public static class Category {
        String name;
    }

    @Test
    void name() {
        TestEntity entity = TestEntity.of("macbook", Category.of("notebook"));
        TestEntity saved = repository.save(entity);

        System.out.println(repository.findById(saved.getId()));

    }

}
