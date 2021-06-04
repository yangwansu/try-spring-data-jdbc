package masil.example.springdata.jdbc.ch9_14_1;


import lombok.Getter;
import masil.example.springdata.jdbc.DataJdbcTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.auditing.IsNewAwareAuditingHandler;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.relational.core.mapping.event.RelationalAuditingCallback;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringJUnitConfig(classes = { ManuallySetupTest.class , ManuallySetupTest.ForAuditing.class} )
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class ManuallySetupTest extends DataJdbcTestSupport {

    public static final LocalDateTime EXPECTED_CREATED_AT = LocalDateTime.of(1978,11,4, 4, 4);

    @Override
    protected String[] getSql() {
        return new String [] {
                "CREATE TABLE IF NOT EXISTS " +
                        "FOO (id bigint primary key identity, " +
                        "     created_at timestamp)"
        };
    }

    //@EnableJdbcAuditing
    @Configuration
    public static class ForAuditing {
        @Bean
        RelationalAuditingCallback isNewAwareAuditingHandler(JdbcMappingContext context) {
            return new RelationalAuditingCallback(new CustomAuditingHandler(context));
        }

        private static class CustomAuditingHandler extends IsNewAwareAuditingHandler {

            public CustomAuditingHandler(JdbcMappingContext context) {
                super(PersistentEntities.of(context));
            }

            @Override
            public Object markAudited(Object source) {
                Foo foo = (Foo) source;
                foo.createdAt = EXPECTED_CREATED_AT;
                return source;
            }
        }
    }

    @Getter
    public static class Foo {
        public static Foo createNew() {
            return new Foo();
        }
        @Id
        private Long id;
        @CreatedDate
        private LocalDateTime createdAt;

    }

    interface FooRepository extends CrudRepository<Foo, Long> { }

    @Autowired
    FooRepository repository;

    @BeforeEach
    void setUp() {
        assertThat(Foo.createNew().getCreatedAt()).isNull();
    }
    @Test
    void manually() {
        Foo saved = repository.save(Foo.createNew());
        assertThat(saved.getCreatedAt()).isEqualTo(EXPECTED_CREATED_AT);
    }
}
