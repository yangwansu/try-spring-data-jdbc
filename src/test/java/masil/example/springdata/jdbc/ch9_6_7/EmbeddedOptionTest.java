package masil.example.springdata.jdbc.ch9_6_7;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import masil.example.springdata.jdbc.DataJdbcTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringJUnitConfig(classes = EmbeddedOptionTest.class)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class EmbeddedOptionTest extends DataJdbcTestSupport {

    @Override
    protected String[] getSql() {
        return new String[] {
                "CREATE TABLE IF NOT EXISTS FOO (" +
                        "id bigint identity primary key," +
                        "name varchar(50)," +
                        "nick_name varchar(50)" +
                        ")"
        };
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_=@PersistenceConstructor)
    public static class Foo {

        public static Foo withNulls() {
            return new Foo(null, null, null);
        }
        public static Foo of(Name name, Name nickname) {
            return new Foo(null, name, nickname);
        }

        @Id
        private Long id;

        @Embedded(onEmpty = Embedded.OnEmpty.USE_EMPTY)
        private final Name name;

        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL, prefix = "nick_")
        private final Name nickname;


        public String getNameString() {
            return getName().getName();
        }

        public String getNickNameString() {
            return getNickname().getName();
        }
    }

    @Value(staticConstructor = "of")
    public static class Name {
        public static final String UNKNOWN = "UNKNOWN";
        String name;

        private Name(String name) {
            this.name = name == null ? UNKNOWN : name;
        }
    }


    interface Repository extends CrudRepository<Foo, Long> {}

    @Autowired
    Repository repository;

    @Test
    @DisplayName("if a name column is null within result set, name field will be set to \"unknown\"")
    void use_empty_option() {
        Foo find = saveAndFind(Foo.withNulls());

        assertThat(find.getNameString()).isEqualTo(Name.UNKNOWN);
    }

    @Test
    @DisplayName("if a nick_name column is null within result set, nickName field will be set to null")
    void use_null_option() {
        Foo find = saveAndFind(Foo.withNulls());

        assertThat(find.getNickname()).isNull();
    }

    @Test
    @DisplayName("Embedded entities are used to have value objects in your java data model, even if there is only one table in your database.")
    void basic() {
        Foo find = saveAndFind(Foo.of(Name.of("Foo"), Name.of("Bar")));

        assertThat(find.getNameString()).isEqualTo("Foo");
        assertThat(find.getNickNameString()).isEqualTo("Bar");
    }

    private Foo saveAndFind(Foo foo) {
        repository.save(foo);

        Foo find = repository.findById(foo.getId()).orElse(null);
        assert find != null;
        return find;
    }
}
