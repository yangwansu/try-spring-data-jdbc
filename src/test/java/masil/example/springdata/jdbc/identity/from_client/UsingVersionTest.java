package masil.example.springdata.jdbc.identity.from_client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import masil.example.springdata.jdbc.AbstractBaseJdbcTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UsingVersionTest {

    public static class Config extends AbstractBaseJdbcTestConfig {
        @Override
        protected String[] getSql() {
            return new String[] {
                "CREATE TABLE IF NOT EXISTS TEST_TABLE (" +
                        "id bigint primary key, " +
                        "version bigint, " +
                        "name varchar(100))"
            };
        }
    }

    @Getter
    @Table("TEST_TABLE")
    @AllArgsConstructor(access = PRIVATE, onConstructor_=@PersistenceConstructor)
    public static class TestEntity {

        public static final Long VERSION_INIT = null;

        public static TestEntity of(Long id, String name) {
            return new TestEntity(id, VERSION_INIT, name);
        }

        @Id
        private final Long id;

        @Version
        private final Long version;

        private final String name;
    }

    interface TestEntityRepository extends CrudRepository<TestEntity, Long>{};

    @Autowired
    TestEntityRepository repository;

    @Test
    @DisplayName("Clients can generate IDs.")
    void generate_by_clients() {
        long id = 1L;

        TestEntity foo = TestEntity.of(id, "Foo");
        TestEntity saved = repository.save(foo);

        assertThat(foo.getVersion()).isNull();
        assertThat(saved.getVersion()).isEqualTo(0L);
    }
}
