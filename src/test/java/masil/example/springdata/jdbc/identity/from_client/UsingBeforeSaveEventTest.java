package masil.example.springdata.jdbc.identity.from_client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import masil.example.springdata.jdbc.AbstractBaseJdbcTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.event.BeforeSaveEvent;
import org.springframework.data.relational.core.mapping.event.RelationalEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringJUnitConfig
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class UsingBeforeSaveEventTest {

    public static class Config extends AbstractBaseJdbcTestConfig {

        AtomicLong atomicLong = new AtomicLong(0L);

        @Override
        protected String[] getSql() {
            return new String[]{
                    "CREATE TABLE IF NOT EXISTS TEST_TABLE ( id bigint primary key, name varchar(100)) "
            };
        }

        @Bean
        public ApplicationListener<?> idInjector() {
            return (ApplicationListener<ApplicationEvent>) (e) -> {
                if (e instanceof BeforeSaveEvent) {
                    BeforeSaveEvent<?> bse = (BeforeSaveEvent<?>) e;
                    if (bse.getType().isAssignableFrom(TestEntity.class)) {
                        if (bse.getEntity() instanceof TestEntity) {
                            TestEntity entity = (TestEntity) bse.getEntity();
                            entity.setId(atomicLong.get());
                        }
                    }
                }

            };
        }
    }

    @Getter
    @Table("TEST_TABLE")
    @AllArgsConstructor(access = PRIVATE, onConstructor_ = @PersistenceConstructor)
    public static class TestEntity {

        public static TestEntity of(String name) {
            return new TestEntity(null, name);
        }

        @Id
        @Setter
        private Long id;
        private String name;
    }

    interface TestEntityRepository extends CrudRepository<TestEntity, Long> {
    }

    @Autowired
    TestEntityRepository repository;

    @Test
    @DisplayName("Set the ID in the application listener before saving.")
    void set_id() {
        TestEntity foo = TestEntity.of("Foo");
        assertThat(foo.getId()).isNull();

        TestEntity saved = repository.save(foo);
        assertThat(saved.getId()).isNotNull();

        Optional<TestEntity> find = repository.findById(saved.getId());
        assertThat(find).get().extracting("id").isNotNull();
    }
}
