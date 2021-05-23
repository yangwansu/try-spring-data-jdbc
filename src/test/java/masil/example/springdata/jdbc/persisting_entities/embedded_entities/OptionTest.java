package masil.example.springdata.jdbc.persisting_entities.embedded_entities;

import lombok.*;
import masil.example.springdata.jdbc.AbstractBaseJdbcTestConfig;
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

@SpringJUnitConfig(classes = OptionTest.class)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class OptionTest extends AbstractBaseJdbcTestConfig{

    @Override
    protected String[] getSql() {
        return new String[] {
                "CREATE TABLE IF NOT EXISTS FOO (" +
                        "id bigint identity primary key," +
                        "name varchar(50)," +
                        "nick_name varchar(50)," +
                        "address varchar(100)," +
                        "phone varchar(100)," +
                        "no integer" +
                        ")"
        };
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_=@PersistenceConstructor)
    public static class Foo {

        public static Foo of(Name name) {
            return new Foo(null, name, null, null);
        }
        public static Foo of(Name name, Name nickname) {
            return new Foo(null, name, nickname, null);
        }

        @Id
        private Long id;

        @Embedded.Empty
        private final Name name;

        @Embedded.Empty(prefix = "nick_")
        private final Name nickname;

        @Embedded.Nullable
        private final AddressInfo addressInfo;

        public String getNameString() {
            return getName().getName();
        }

        public String getNickNameString() {
            return getNickname().getName();
        }
    }

    @Value
    @ToString
    public static class Name {
        public static final String UNKNOWN = "UNKNOWN";
        String name;

        public Name() {
            this.name = UNKNOWN;
        }

        @PersistenceConstructor
        Name(String name) {
            this.name = name;
        }


        public static Name of(String name) {
            return new Name(name);
        }
    }

    @Value(staticConstructor = "of")
    public static class AddressInfo {
        String address;
        String phone;
        int no;
    }

    interface Repository extends CrudRepository<Foo, Long> {}

    @Autowired
    Repository repository;

    @Test
    void name() {
        Foo foo = Foo.of(Name.of("Foo"));

        repository.save(foo);

        Foo find = repository.findById(foo.getId()).orElse(null);
        assert find != null;

        assertThat(find.getNameString()).isEqualTo("Foo");
        assertThat(find.getNickNameString()).isNull(); // ??????
        assertThat(find.getAddressInfo().getAddress()).isNull(); // ??????
        assertThat(find.getAddressInfo().getPhone()).isNull(); // ??????
        assertThat(find.getAddressInfo().getNo()).isEqualTo(0L); // ??????



    }
}
