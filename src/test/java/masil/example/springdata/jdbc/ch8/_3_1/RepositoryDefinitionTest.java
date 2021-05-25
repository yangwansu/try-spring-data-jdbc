package masil.example.springdata.jdbc.ch8._3_1;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import masil.example.springdata.jdbc.AbstractBaseJdbcTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringJUnitConfig(classes = RepositoryDefinitionTest.class)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class RepositoryDefinitionTest extends AbstractBaseJdbcTestConfig {

    @Override
    protected String[] getSql() {
        return new String [] {
                "CREATE TABLE IF NOT EXISTS FOO (id integer identity primary key, name varchar(10))"
        };
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_=@PersistenceConstructor)
    public static class Foo {

        public static Foo of(String name) {
            return new Foo(null, name);
        }

        @Id
        private Integer id;
        private String name;
    }

    /**
     *
     *
     * @see org.springframework.data.repository.CrudRepository#save(Object)
     * @see org.springframework.data.repository.CrudRepository#findById(Object)
     */
    @RepositoryDefinition(domainClass = Foo.class, idClass = Integer.class)
    interface FooRepository {

        Foo save(Foo foo);

        Foo findById(Integer id);
    }

    @Autowired
    FooRepository repository;

    @Test
    void fine_tuning_repository() {

        Foo newFoo = Foo.of("Foo");
        Foo saved = repository.save(newFoo);

        assertThat(saved).isSameAs(newFoo);
        assertThat(newFoo.getId()).isNotNull();

        Foo find = repository.findById(saved.getId());

        assertThat(find.getId()).isEqualTo(saved.getId());
    }

    @NoRepositoryBean
    interface CustomBaseRepository<T,ID> extends Repository<T, ID> {
        Foo save(Foo foo);

        Foo findById(Integer id);
    }

    interface Foo2Repository extends CustomBaseRepository<Foo, Integer> {

    }

    @Autowired
    Foo2Repository repository2;

    @Test
    void fine_tuning_repository2() {

        Foo newFoo = Foo.of("Foo");
        Foo saved = repository2.save(newFoo);

        assertThat(saved).isSameAs(newFoo);
        assertThat(newFoo.getId()).isNotNull();

        Foo find = repository2.findById(saved.getId());

        assertThat(find.getId()).isEqualTo(saved.getId());
    }

}
