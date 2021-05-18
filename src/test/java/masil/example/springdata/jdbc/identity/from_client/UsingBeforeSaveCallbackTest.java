package masil.example.springdata.jdbc.identity.from_client;


import lombok.AllArgsConstructor;
import lombok.Getter;
import masil.example.springdata.jdbc.AbstractBaseJdbcTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.conversion.AggregateChange;
import org.springframework.data.relational.core.conversion.MutableAggregateChange;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.event.BeforeSaveCallback;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;
import java.util.Random;

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@SpringJUnitConfig
public class UsingBeforeSaveCallbackTest {

    public static class Config extends AbstractBaseJdbcTestConfig {
        @Override
        protected String[] getSql() {
            return new String[]{
                    "CREATE TABLE IF NOT EXISTS TEST_TABLE(id bigint primary key, name varchar(100))"
            };
        }

        @Bean
        BeforeSaveCallback<TestEntity> idInjector() {
            return new TestEntity.IdInjector();
        }
    }

    @Getter
    @Table("TEST_TABLE")
    @AllArgsConstructor(access = PRIVATE, staticName = "of", onConstructor_ = @PersistenceConstructor)
    public static class TestEntity {

        public static TestEntity of(String name) {
            return new TestEntity(null, name);
        }

        @Id
        private Long id;
        private final String name;

        public static class IdInjector implements BeforeSaveCallback<TestEntity> {
            @Override
            public TestEntity onBeforeSave(TestEntity aggregate, MutableAggregateChange<TestEntity> aggregateChange) {

                if(aggregateChange.getKind() == AggregateChange.Kind.SAVE && isNew(aggregate)) {
                    aggregate.id = generateId();
                }
                return aggregate;
            }

            private Long generateId() {
                return new Random().nextLong();
            }

            private boolean isNew(TestEntity aggregate) {
                return aggregate.id == null;
            }

        }
    }

    interface TestEntityRepository extends CrudRepository<TestEntity, Long> {
    }

    @Autowired
    TestEntityRepository repository;

    @Test
    @DisplayName("Set the ID in the callback before saving.")
    void set_id() {
        TestEntity foo = TestEntity.of("Foo");
        assertThat(foo.getId()).isNull();

        TestEntity saved = repository.save(foo);
        assertThat(saved.getId()).isNotNull();

        Optional<TestEntity> find = repository.findById(saved.getId());
        assertThat(find).get().extracting("id").isNotNull();
    }
}
