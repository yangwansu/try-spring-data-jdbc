package masil.example.springdata.jdbc.relations;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import masil.example.springdata.jdbc.DataJdbcConfiguration;

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
    @Table("CATEGORY_SET")
    public static class Category {
        String name;
    }

    @Test
    @DisplayName("Using Set Collection")
    void name() {

        Project project = Project.of("ibm")
                .addCategory(Category.of("notebook"));

        project = operations.save(project);

        System.out.println(operations.findById(project.getId(), Project.class));
    }

    @Getter
    @Table("PRODUCT")
    @AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_=@PersistenceConstructor)
    public static class ProjectMap {

        public static ProjectMap of(String name){
            return new ProjectMap(null, name, new HashMap<>());
        }

        @Id
        private final Long id;
        private final String name;

        @Column("PRODUCT_ID")
        private final Map<String, Category__> category;

        private ProjectMap(String name, Map<String, Category__> category) {
            this(null, name, category);
        }

        public ProjectMap addCategory(Category__ category){
            Map<String, Category__> categoryMap = new HashMap<>(getCategory());
            categoryMap.put(category.getName(), category);

            return new ProjectMap(getId(), getName(), categoryMap);
        }
    }

    @Value(staticConstructor = "of")
    @Table("CATEGORY_MAP")
    public static class Category__ {
        String name;
    }

    @Test
    @DisplayName("Using Map Collection")
    void OneToMany_test() {

        Category__ notebook = Category__.of("notebook");
        ProjectMap projectMap = ProjectMap.of("ibm")
            .addCategory(notebook);

        // CREATE TABLE IF NOT EXISTS category_map (PRODUCT_ID INTEGER, PRODUCT_KEY varchar (100), name varchar(100))
        ProjectMap saved = operations.save(projectMap);
        ProjectMap found = operations.findById(saved.getId(), ProjectMap.class);

        assert found != null;

        assertThat(found.getCategory().get("notebook"))
            .isEqualTo(notebook);

    }

    @Getter
    @Table("PRODUCT")
    @AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_=@PersistenceConstructor)
    public static class ProjectList {

        public static ProjectList of(String name){
            return new ProjectList(null, name, Lists.emptyList());
        }

        @Id
        private final Long id;
        private final String name;

        @Column("PRODUCT_ID")
        private final List<Category_> categories;

        private ProjectList(String name,
            List<Category_> categories) {
            this(null, name, categories);
        }

        public ProjectList addCategory(Category_ category){
            List<Category_> categories = new ArrayList<>(getCategories());
            categories.add(category);
            return new ProjectList(getId(), getName(), categories);
        }
    }

    @Value(staticConstructor = "of")
    @Table("CATEGORY_LIST")
    public static class Category_ {
        String name;
    }

    @Test
    @DisplayName("Using List Collection")
    void oneToMany_list_test() {

        Category_ notebook = Category_.of("notebook_list");
        ProjectList project = ProjectList.of("ibm")
            .addCategory(notebook);

        //CREATE TABLE IF NOT EXISTS category_list (PRODUCT_ID INTEGER, PRODUCT_KEY varchar (100), name varchar(100))
        ProjectList saved = operations.save(project);
        ProjectList found = operations.findById(saved.getId(), ProjectList.class);

        assert found != null;

        assertThat(found.getCategories().get(0)).isEqualTo(notebook);
    }
}
