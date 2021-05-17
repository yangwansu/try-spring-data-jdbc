package masil.example.springdata.jdbc.relations;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import masil.example.springdata.jdbc.AbstractBaseJdbcTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = OneToOneTest.Config.class)
public class OneToOneTest {

    public static class Config extends AbstractBaseJdbcTestConfig {
        @Override
        protected String getScript() {
            return "schema.sql";
        }
    }


    @Autowired
    JdbcAggregateOperations operations;

    @Getter
    @Table("PRODUCT")
    @AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_=@PersistenceConstructor)
    public static class Project {

        public static Project of(String name, Category category) {
            return new Project(name, category);
        }

        @Id
        private final Long id;
        private final String name;

        @Column("PRODUCT_ID")
        private final Category category;

        private Project(String name, Category category) {
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
        Project product = Project.of("macbook", Category.of("notebook"));
        Project project = operations.save(product);

        System.out.println(operations.findById(project.getId(), Project.class));

    }

}
