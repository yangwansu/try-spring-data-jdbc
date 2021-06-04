package masil.example.springdata.jdbc.ch9_6_8;


import lombok.AllArgsConstructor;
import lombok.Getter;
import masil.example.springdata.jdbc.DataJdbcTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@SpringJUnitConfig(classes = VersionUpdateTest.class)
public class VersionUpdateTest extends DataJdbcTestSupport {

    public static final long FIRST_VERSION = 0L;

    @Override
    protected String[] getSql() {
        return new String[]{
                "CREATE TABLE IF NOT EXISTS CART ( id bigint primary key identity, version bigint, name varchar(100) ) ",
                "CREATE TABLE IF NOT EXISTS ITEM ( CART_ID bigint, name varchar(100), count integer) ",
        };
    }

    @Table
    @Getter
    @AllArgsConstructor(access = PRIVATE, onConstructor_ = @PersistenceConstructor)
    public static class Cart {

        public static Cart of(String name) {
            return new Cart(null, null, name, new HashSet<>());
        }

        @Id
        private Long id;
        @Version
        private Long version;

        private String name;

        @Column("CART_ID")
        private final Set<Item> items;

        public Set<Item> getItems() {
            return Collections.unmodifiableSet(items);
        }

        public void addItem(Item item) {
            items.add(item);
        }

        public void rename(String name) {
            this.name = name;
        }

        public void updateItemCount(String name, int count) {
            getItems().stream().filter(i -> name.equals(i.getName())).forEach(i -> i.update(count));

        }
    }

    @Getter
    @AllArgsConstructor(access = PRIVATE)
    public static class Item {

        public static Item create(String name, int count) {
            return new Item(name, count);
        }

        final String name;
        int count;

        void update(int count) {
            this.count = count;
        }
    }

    interface OrderRepository extends CrudRepository<Cart, Long> {
    }

    @Autowired
    OrderRepository repository;

    private Cart saved;

    @BeforeEach
    void setUp() {
        Cart cart = Cart.of("My Cart");
        cart.addItem(Item.create("Apple", 1));

        saved = repository.save(cart);
        assertThat(saved.getVersion()).isEqualTo(FIRST_VERSION);
    }

    @Test
    @DisplayName("The version is always updated on save")
    void updateVersion() {
        assertThat(update((cart) -> cart.rename("Our Cart"))
                .getVersion()).isEqualTo(1L);
        assertThat(update((cart) -> cart.addItem(Item.create("Tomato", 1)))
                .getVersion()).isEqualTo(2L);
        assertThat(update((cart) -> cart.updateItemCount("Tomato", 2))
                .getVersion()).isEqualTo(3L);
        assertThat(update((cart) -> { /*do nothing*/})
                .getVersion()).isEqualTo(4L);
        assertThat(update((cart) -> { /*do nothing*/})
                .getVersion()).isEqualTo(5L);
    }

    private Cart update(Consumer<Cart> f) {
        f.accept(saved);
        repository.save(saved);
        return saved;
    }

}
