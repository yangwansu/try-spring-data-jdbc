package masil.example.springdata.jdbc;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = AggregateReferenceTest.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AggregateReferenceTest extends DataJdbcTestSupport{


    @Override
    protected String[] getSql() {
        return new String[] {
                "CREATE TABLE IF NOT EXISTS Movie (id bigint primary key identity, title varchar(100))",
                "CREATE TABLE IF NOT EXISTS Movie_Actor (movie bigint, actor bigint)",
                "CREATE TABLE IF NOT EXISTS Actor (id bigint primary key identity, name varchar(100))"

        };
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_=@PersistenceConstructor)
    public static class Movie {
        public static Movie create(String title, Actor ... actor) {
            Set<MovieActor> movieActor = Arrays.stream(actor).map(a -> new MovieActor(AggregateReference.to(a.getId()))).collect(Collectors.toSet());

            return new Movie(null, title, movieActor );
        }

        public static Movie create(String title, MovieActor ... movieActor) {
            return new Movie(null, title, Collections.unmodifiableSet(Arrays.stream(movieActor).collect(Collectors.toSet())));
        }

        @Id
        private Long id;
        private final String title;
        private final Set<MovieActor> movieActors;
    }

    @Getter
    @AllArgsConstructor
    public static class MovieActor {
        private final AggregateReference<Actor, Long> actor;
    }

    @Getter
    @AllArgsConstructor
    @Table("MOVIE_ACTOR")
    public static class ActorMovie {
        private final AggregateReference<Movie, Long> movie;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_=@PersistenceConstructor)
    public static class Actor {
        public static Actor of(String name) {
            return new Actor(null, name, Sets.newHashSet());
        }
        @Id
        private Long id;
        private final String name;
        private final Set<ActorMovie> actorMovies;
    }

    interface MovieRepository extends CrudRepository<Movie, Long> {}
    interface ActorRepository extends CrudRepository<Actor, Long> {}

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    ActorRepository actorRepository;

    private Long emmaId;

    @BeforeEach
    void setUp() {
        Actor emma = Actor.of("Emma Stone");
        actorRepository.save(emma);
        Movie lalaland = Movie.create("LaLa Land", emma);
        movieRepository.save(lalaland);

        emmaId = emma.getId();
    }

    @Test
    @DisplayName("Emma Stone is the lead actress in La La Land.")
    void Emma_Stone_is_appeared_in_The_Lala_land() {
        Optional<Actor> find = actorRepository.findById(emmaId);

        assertThat(find).isPresent().get()
                .extracting(a-> a.getActorMovies().stream()
                .map(i->movieRepository.findById(Objects.requireNonNull(i.getMovie().getId())))
                .filter(Optional::isPresent)
                .map(i->i.orElse(null).getTitle())
                .anyMatch("LaLa Land"::equals)).isEqualTo(true);

    }
}
