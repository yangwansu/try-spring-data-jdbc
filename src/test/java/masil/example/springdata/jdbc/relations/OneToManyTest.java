package masil.example.springdata.jdbc.relations;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import masil.example.springdata.jdbc.DataJdbcConfiguration;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.HashSet;
import java.util.Set;

@SpringJUnitConfig(classes = DataJdbcConfiguration.class)
public class OneToManyTest {

    @Autowired
    JdbcAggregateOperations operations;

    @Getter
    @Table("PRODUCT")
    @AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_=@PersistenceConstructor)
    public static class Project {

        public static Project of(String name) {
            return new Project(name, Sets.newHashSet());
        }

        @Id
        private final Long id;
        private final String name;

        @Column("PRODUCT_ID")
        private final Set<Category> categories;

        private Project(String name, Set<Category> category) {
            this(null, name, category);
        }

        public Project addCategory(Category category) {
            Set<Category> categories = new HashSet<>(getCategories());
            categories.add(category);

            return new Project(getId(), getName(), categories);
        }
    }

    @Value(staticConstructor = "of")
    @Table("CATEGORY")
    public static class Category {
        String name;
    }

    @Test
    void name() {

        Project project = Project.of("ibm")
                .addCategory(Category.of("notebook"));

        project = operations.save(project);

        System.out.println(operations.findById(project.getId(), Project.class));
    }

}
