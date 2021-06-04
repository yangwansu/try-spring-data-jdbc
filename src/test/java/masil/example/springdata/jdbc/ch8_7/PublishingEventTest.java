package masil.example.springdata.jdbc.ch8_7;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import masil.example.springdata.jdbc.DataJdbcTestSupport;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Collection;

@SpringJUnitConfig(classes = PublishingEventTest.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PublishingEventTest extends DataJdbcTestSupport {

    @Override
    protected String[] getSql() {
        return new String[] {
            "CREATE TABLE IF NOT EXISTS Foo (id bigint primary key identity, name varchar(100))"
        };
    }

    @Bean
    ApplicationListener<FooDomainEvent> fooApplicationListener() {
        return event -> {
            System.out.println(event);
        };
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @RequiredArgsConstructor(staticName = "of")
    public static class Foo {
        @Id
        private Long id;
        private final String name;

        @DomainEvents
        Collection<Object> domainEvent() {
            return Lists.newArrayList(new FooDomainEvent(this));
        }

        @AfterDomainEventPublication
        void callbackMethod() {
            System.out.println("xxx");
        }
    }

    public static class FooDomainEvent extends ApplicationEvent {

        /**
         * Create a new {@code ApplicationEvent}.
         *
         * @param source the object on which the event initially occurred or with
         *               which the event is associated (never {@code null})
         */
        public FooDomainEvent(Object source) {
            super(source);
        }
    }

    interface FooRepository extends CrudRepository<Foo, Long> {}

    @Autowired
    FooRepository repository;

    @Test
    void name() {

        repository.save(Foo.of("Foo"));

    }
}
