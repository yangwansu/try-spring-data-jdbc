package masil.example.springdata.jdbc.ch9_7;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import masil.example.springdata.jdbc.DataJdbcTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

@SpringJUnitConfig
public class QueryToAggregateReferenceTest {

    public static class Config extends DataJdbcTestSupport {

        @Override
        protected String[] getSql() {
            return new String [] {
              "CREATE TABLE IF NOT EXISTS Book (id bigint identity primary key, author_id bigint)",
              "CREATE TABLE IF NOT EXISTS Author (id bigint identity primary key, name varchar(100))"
            };
        }
    }
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_=@PersistenceConstructor)
    public static class Author {
        public static Author of(String name) {
            return new Author(null, name);
        }
        @Id
        private Long id;
        private String name;
    }
    interface AuthorRepository extends CrudRepository<Author, Long> {}

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_=@PersistenceConstructor)
    public static class Book {

        public static Book of(Author author) {
            return new Book(null, AggregateReference.to(author.id));
        }

        @Id
        private Long id;

        private AggregateReference<Author, Long>  authorId;
    }
    interface BookRepository extends CrudRepository<Book, Long> {

        List<Book> findBooksByAuthorId(Long  authorId);
    }

    @Autowired
    AuthorRepository authorRepository;
    @Autowired
    BookRepository bookRepository;

    @Test
    void can_not_entrance_here() {
        Author author = authorRepository.save(Author.of("Van"));

        Book book = bookRepository.save(Book.of(author));

        List<Book> list = bookRepository.findBooksByAuthorId(author.id);

    }
}
