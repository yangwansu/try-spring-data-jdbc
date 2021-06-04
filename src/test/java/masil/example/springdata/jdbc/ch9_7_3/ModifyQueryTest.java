package masil.example.springdata.jdbc.ch9_7_3;

import lombok.AllArgsConstructor;
import lombok.Getter;
import masil.example.springdata.jdbc.AbstractBaseJdbcTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringJUnitConfig(classes = ModifyQueryTest.class)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class ModifyQueryTest extends AbstractBaseJdbcTestConfig {

    public static final Integer INVALID_ID = 100;

    @Override
    protected String[] getSql() {
        return new String[]{
            "CREATE TABLE IF NOT EXISTS FOO (" +
                    "id integer identity primary key," +
                    "value integer" +
                    ")"
        };
    }

    @Getter
    @AllArgsConstructor(access = PRIVATE, onConstructor_=@PersistenceConstructor)
    public static class Foo {

        public static Foo of(int value) {
            return new Foo(null, value);
        }

        @Id
        private Integer id;
        private Integer value;
    }

    interface FooRepository extends CrudRepository<Foo, Integer> {

        @Modifying
        @Query("UPDATE FOO SET VALUE = :value WHERE ID = :id")
        int updateValueAndReturnInteger(@Param("id") Integer id, @Param("value") Integer value);

        @Modifying
        @Query("UPDATE FOO SET VALUE = :value WHERE ID = :id")
        int updateValueAndReturnBoolean(@Param("id") Integer id,@Param("value") Integer value);
    }

    @Autowired
    FooRepository repository;


    @Test
    void updateQuery() {
        Foo foo = Foo.of(0);
        Foo saved = repository.save(foo);

        int updatedCount = repository.updateValueAndReturnInteger(saved.getId(), 100);
        assertThat(updatedCount).isEqualTo(1);

        updatedCount = repository.updateValueAndReturnInteger(INVALID_ID, 100);
        assertThat(updatedCount).isEqualTo(0);
    }
}
