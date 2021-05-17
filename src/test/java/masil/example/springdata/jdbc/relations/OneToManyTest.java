package masil.example.springdata.jdbc.relations;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import masil.example.springdata.jdbc.AbstractBaseJdbcTestConfig;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@SpringJUnitConfig(classes = OneToManyTest.Config.class)
public class OneToManyTest {

    public static class Config extends AbstractBaseJdbcTestConfig {
        @Override
        protected String getScript() {
            return "schema.sql";
        }
    }


    @Autowired
    TestEntityRepository repository;

    interface TestEntityRepository extends CrudRepository<TestEntity, Long> { }

    @Getter
    @Table("PRODUCT")
    @AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_=@PersistenceConstructor)
    public static class TestEntity {

        public static TestEntity of(String name) {
            return new TestEntity(name, Sets.newHashSet());
        }

        @Id
        private final Long id;
        private final String name;

        @Column("PRODUCT_ID")
        private final Set<Category> categories;

        private TestEntity(String name, Set<Category> category) {
            this(null, name, category);
        }

        public TestEntity addCategory(Category category) {
            Set<Category> categories = new HashSet<>(getCategories());
            categories.add(category);

            return new TestEntity(getId(), getName(), categories);
        }
    }

    @Value(staticConstructor = "of")
    @Table("CATEGORY")
    public static class Category {
        String name;
    }

    @Test
    void name() {
        TestEntity entity = TestEntity.of("ibm")
                .addCategory(Category.of("notebook"));

        entity = repository.save(entity);

        System.out.println(repository.findById(entity.getId()));
    }

}
