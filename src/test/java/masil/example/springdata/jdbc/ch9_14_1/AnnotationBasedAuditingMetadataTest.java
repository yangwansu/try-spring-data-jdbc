package masil.example.springdata.jdbc.ch9_14_1;

import lombok.Getter;
import masil.example.springdata.jdbc.DataJdbcTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;


@SpringJUnitConfig(classes = {AnnotationBasedAuditingMetadataTest.class, AnnotationBasedAuditingMetadataTest.ForAuditing.class})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class AnnotationBasedAuditingMetadataTest extends DataJdbcTestSupport {

    @Override
    protected String[] getSql() {
        return new String [] {
          "CREATE TABLE IF NOT EXISTS FOO (id bigint primary key identity," +
                  "created_at timestamp, " +
                  "LAST_MODIFIED_AT timestamp)"
        };
    }

    @EnableJdbcAuditing
    public static class ForAuditing {}

    @Getter
    public static class Foo {
        public static Foo createNew() {
            return new Foo();
        }
        @Id
        private Long id;
        @CreatedDate
        private LocalDateTime createdAt;

        @LastModifiedDate
        private LocalDateTime lastModifiedAt;
    }

    interface FooRepository extends CrudRepository<Foo, Long> { }

    @Autowired
    FooRepository repository;

    @BeforeEach
    void setUp() {
        assertThat(Foo.createNew().getCreatedAt()).isNull();
        assertThat(Foo.createNew().getLastModifiedAt()).isNull();
    }

    @Test
    void auditing() {
        Foo saved = repository.save(Foo.createNew());
        assertThat(saved.getCreatedAt()).isEqualToIgnoringSeconds(LocalDateTime.now());
        assertThat(saved.getLastModifiedAt()).isEqualToIgnoringSeconds(LocalDateTime.now());
    }

}
