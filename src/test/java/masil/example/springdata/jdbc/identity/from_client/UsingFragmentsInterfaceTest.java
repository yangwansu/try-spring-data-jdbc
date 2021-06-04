package masil.example.springdata.jdbc.identity.from_client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import masil.example.springdata.jdbc.DataJdbcTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringJUnitConfig(classes = UsingFragmentsInterfaceTest.class)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class UsingFragmentsInterfaceTest extends DataJdbcTestSupport {

    @Override
    protected String[] getSql() {
        return new String[] {
                "CREATE TABLE IF NOT EXISTS TEST_TABLE ( id bigint primary key , name varchar(100)) "
        };
    }

    @Getter
    @Table("TEST_TABLE")
    @AllArgsConstructor(staticName = "of")
    public static class TestEntity {
        @Id
        private Long id;

        private String name;
    }

    interface TestEntityRepository extends CrudRepository<TestEntity, Long> , WithInsert<TestEntity> {}

    public interface WithInsert<T> {
        T insert(T t);
    }

    public static class WithInsertImpl<T> implements WithInsert<T> {

        private final JdbcAggregateTemplate template;

        public WithInsertImpl(JdbcAggregateTemplate template) {
            this.template = template;
        }

        @Override
        public T insert(T t) {
            return template.insert(t);
        }
    }

    @Autowired
    TestEntityRepository repository;

    @Test
    void insert_using_fragment_interface() {

        assertThatThrownBy(() -> repository.save(TestEntity.of(1L, "Foo"))); //Fail! UPDATE

        repository.insert(TestEntity.of(1L, "Foo"));

    }
}
