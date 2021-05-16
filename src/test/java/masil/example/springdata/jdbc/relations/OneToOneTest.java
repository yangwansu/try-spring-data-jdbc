package masil.example.springdata.jdbc.relations;

import static org.assertj.core.api.Assertions.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import masil.example.springdata.jdbc.DataJdbcConfiguration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = DataJdbcConfiguration.class)
public class OneToOneTest {

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
        Project saved = operations.save(product);
        assertThat(saved.getName()).isSameAs(product.getName());
        assertThat(saved.getCategory()).isSameAs(product.getCategory());

        Project found = operations.findById(saved.getId(), Project.class);
        assert found != null;
        assertThat(found.getId()).isNotNull();

    }

}
