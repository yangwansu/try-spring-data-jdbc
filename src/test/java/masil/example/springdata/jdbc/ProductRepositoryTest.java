package masil.example.springdata.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = ProductRepositoryTest.Config.class)
public class ProductRepositoryTest {


    public static class Config extends AbstractBaseJdbcTestConfig {
        @Override
        protected String getScript() {
            return "schema.sql";
        }
    }

    @Autowired
    ProductRepository repository;


    @BeforeEach
    void setUp() {
        assertThat(repository.count()).isZero();

        Iterable<Product> iter = repository.findAll();
        assertThat(iter.iterator().hasNext()).isFalse();
    }

    @Test
    void save() {

        Product macbook = Product.of("Macbook");
        Product saved = repository.save(macbook);

        assertThat(saved).isNotSameAs(macbook);

        assertThat(macbook.getId()).isNull();
        assertThat(saved.getId()).isNotNull();

        Optional<Product> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get()).isNotSameAs(saved);

    }
}
