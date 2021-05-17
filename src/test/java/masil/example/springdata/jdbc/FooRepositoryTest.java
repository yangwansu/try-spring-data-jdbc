package masil.example.springdata.jdbc;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@Transactional
@SpringJUnitConfig(classes = FooRepositoryTest.Config.class)
public class FooRepositoryTest {

    public static class Config extends AbstractBaseJdbcTestConfig {
        @Override
        protected String getScript() {
            return "schema.sql";
        }
    }

    interface MissingIdProductRepository extends CrudRepository<MissingIdProduct, Long> { }
    interface MissingSetterProductRepository extends CrudRepository<MissingSetterProduct, Long> { }
    interface UnknownRepository extends CrudRepository<Unknown, Long> { }
    interface FooRepository extends CrudRepository<Foo, Long> { }
    interface BarRepository extends CrudRepository<Bar, Long> { }


    @BeforeEach
    void setUp() {
        assertThat(fooRepository.count()).isZero();

        Iterable<Foo> iter = fooRepository.findAll();
        assertThat(iter.iterator().hasNext()).isFalse();
    }

    @Autowired
    UnknownRepository unknownRepository;
    @Autowired
    MissingIdProductRepository missingIdProductRepository;

    public static class Unknown {

    }

    @Table("PRODUCT")
    public static class MissingIdProduct {
        private Long id;
        private String name;
    }


    @Test
    @Rollback
    void when_save_with() {
        assertThatThrownBy(()-> unknownRepository.save(new Unknown()))
                .isInstanceOf(DbActionExecutionException.class)
                .hasRootCauseMessage("user lacks privilege or object not found: UNKNOWN");


        assertThatThrownBy(()-> missingIdProductRepository.save(new MissingIdProduct()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("After saving the identifier must not be null!");

    }

    @Autowired
    MissingSetterProductRepository missingSetterProductRepository;

    @Table("PRODUCT")
    @Getter
    public static class MissingSetterProduct {
        @Id
        private final Long id;
        private final String name;

        public MissingSetterProduct(String name) {
            this.id = null;
            this.name = name;
        }
    }

    @Test
    @Rollback
    void name() {

        MissingSetterProduct saved = missingSetterProductRepository.save(new MissingSetterProduct("macbook"));
        assertThat(saved.getId()).isZero();

        Optional<MissingSetterProduct> found = missingSetterProductRepository.findById(saved.getId());

        assertThat(found).get().hasFieldOrPropertyWithValue("name", "macbook");

    }

    @Autowired
    FooRepository fooRepository;

    @Table("PRODUCT")
    @Getter
    @Setter
    public static class Foo {

        public Foo() {
        }

        @Id
        private Long id;
        private String name;
    }

    @Test
    @Rollback
    void saveFoo() {

        Foo macbook = new Foo();
        macbook.setName("macbook");
        Foo saved = fooRepository.save(macbook);
        Optional<Foo> found = fooRepository.findById(saved.getId());

        assertThat(saved).isSameAs(macbook);
        assertThat(saved.getId()).isNotNull();
        assertThat(found).isPresent();
    }


    @Autowired
    BarRepository barRepository;

    @Table("PRODUCT")
    @Getter
    @Setter
    public static class Bar {

        @Id
        private Long id;
        private final String name;

        public Bar(String name) {
            this.name = name;
        }
    }

    @Test
    @Rollback
    void saveBar() {

        Bar macbook = new Bar("macbook");
        Bar saved = barRepository.save(macbook);
        Optional<Bar> found = barRepository.findById(saved.getId());

        assertThat(saved).isSameAs(macbook);
        assertThat(saved.getId()).isNotNull();
        assertThat(found).isPresent();

    }
}

