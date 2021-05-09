package masil.example.springdata.jdbc;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product {

    public static Product of(String name) {
        return new Product( null, name);
    }

    @Id
    private final Long id;
    private final String name;

}
