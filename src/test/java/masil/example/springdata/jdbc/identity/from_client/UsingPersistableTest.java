package masil.example.springdata.jdbc.identity.from_client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import masil.example.springdata.jdbc.DataJdbcTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.event.AfterSaveCallback;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;


@SpringJUnitConfig(classes = UsingPersistableTest.class)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class UsingPersistableTest extends DataJdbcTestSupport {
    @Override
    protected String[] getSql() {
        return new String[]{
                "CREATE TABLE IF NOT EXISTS TEST_TABLE (" +
                        "id bigint primary key, " +
                        "name varchar(100))"
        };
    }

    @Bean
    AfterSaveCallback<TestEntity> afterNewInsertCallback() {
        return new TestEntity.AfterNewInsertCallback();
    }

    @Getter
    @Table("TEST_TABLE")
    @AllArgsConstructor(access = PRIVATE)
    public static class TestEntity implements Persistable<Long> {

        private static final boolean NEW = true;

        public static TestEntity of(Long id, String name) {
            return new TestEntity(id, name, NEW);
        }

        @Id
        private final Long id;

        private String name;

        @Transient
        private final boolean isNew; // default is false;

        @PersistenceConstructor
        private TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
            this.isNew = true;
        }

        @Override
        public boolean isNew() {
            return isNew;
        }

        public static class AfterNewInsertCallback implements AfterSaveCallback<TestEntity> {

            @Override
            public TestEntity onAfterSave(TestEntity aggregate) {
                if (aggregate.isNew()) {
                    return new TestEntity(aggregate.getId(), aggregate.getName(), false);
                }

                return aggregate;
            }
        }
    }

    interface TestEntityRepository extends CrudRepository<TestEntity, Long> { }

    @Autowired
    TestEntityRepository repository;

    @Test
    @DisplayName("Set The ID in the client code block to a Constructor of the Class")
    void set_id() {
        TestEntity foo = TestEntity.of(1L, "Foo");
        assertThat(foo.isNew()).isTrue();

        TestEntity saved = repository.save(foo);  //INSERT
        assertThat(saved.isNew()).isFalse();      //see AfterNewInsertCallback#onAfterSave

        saved = repository.save(saved);           //UPDATE
        assertThat(saved.isNew()).isFalse();      //see AfterNewInsertCallback#onAfterSave
    }
}
