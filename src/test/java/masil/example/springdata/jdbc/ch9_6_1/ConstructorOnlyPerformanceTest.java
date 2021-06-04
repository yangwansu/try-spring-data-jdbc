package masil.example.springdata.jdbc.ch9_6_1;

import lombok.Getter;
import lombok.Setter;
import masil.example.springdata.jdbc.DataJdbcTestSupport;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.StopWatch;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.IntStream;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;


@SpringJUnitConfig(classes = ConstructorOnlyPerformanceTest.class)
@DisplayName("Constructor-only materialization is up to 30% faster than properties population.")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class ConstructorOnlyPerformanceTest extends DataJdbcTestSupport {

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS TEST_TABLE%d (id bigint identity primary key, name varchar(100))";
    public static final int TABLE_COUNT = 2;

    @Override
    protected String[] getSql() {
        int count = 1000;
        return makeSqlStrings(count);
    }

    private String[] makeSqlStrings(int count) {
        String[] sql = new String[(count * TABLE_COUNT) + TABLE_COUNT];
        sql[0] = String.format(CREATE_TABLE, 1);
        sql[1] = String.format(CREATE_TABLE, 2);

        IntStream.range(2, count+2).forEach(i -> {
            sql[i] = String.format("INSERT INTO TEST_TABLE%d (name) VALUES('name')", 1);
        });

        IntStream.range(count+2, (count+2)+count).forEach(i -> {
            sql[i] = String.format("INSERT INTO TEST_TABLE%d (name) VALUES('name')",  2);
        });
        return sql;
    }

    @Getter
    @Table("TEST_TABLE1")
    public static class Foo {
        @Id
        private Long id;
        private String name;

        public Foo(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }


    @Getter
    @Setter
    @Table("TEST_TABLE2")
    public static class Bar {
        @Id
        private Long id;
        private String name;

        public Bar() {
        }
    }

    @Autowired
    FooRepository fooRepository;

    interface FooRepository extends CrudRepository<Foo, Long> {
    }

    @Autowired
    BarRepository barRepository;

    interface BarRepository extends CrudRepository<Bar, Long> {
    }

    public static List<StopWatch> stopWatches = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
        Logger.getRootLogger().setLevel(Level.OFF);
        Logger.getLogger("org.springframework.jdbc.core.JdbcTemplate").setLevel(Level.OFF);
    }

    @AfterAll
    static void afterAll() {
        Map<String, Long> map = new HashMap<>();
        map = stopWatches.stream().reduce(map, (map12, stopWatch) -> {
            Arrays.stream(stopWatch.getTaskInfo()).forEach(t->{
                if(map12.containsKey(t.getTaskName())) {
                    long ms = map12.get(t.getTaskName());
                    map12.put(t.getTaskName(), ms + t.getTimeMillis());
                }else {
                    map12.put(t.getTaskName(), t.getTimeMillis());
                }
            });
            return map12;
        }, (map1, map2) -> null);

        long total = map.values().stream().reduce(Long::sum).orElse(0L);
        StringBuilder sb = new StringBuilder();
        NumberFormat pf = NumberFormat.getPercentInstance();
        pf.setMinimumIntegerDigits(3);
        pf.setGroupingUsed(false);
        for (Map.Entry<String, Long> task : map.entrySet()) {
            sb.append(task.getKey()).append("  ");
            sb.append(pf.format((double) task.getValue() / total)).append("  ");
        }

        System.out.println(sb);
    }

    @RepeatedTest(50)
    void test1() {
        StopWatch sw = new StopWatch();

        sw.start("foo");
        fooRepository.findAll();
        fooRepository.findAll();
        sw.stop();
        sw.start("bar");
        barRepository.findAll();
        barRepository.findAll();
        sw.stop();

        stopWatches.add(sw);

    }

    @RepeatedTest(50)
    void test2() {
        StopWatch sw = new StopWatch();

        sw.start("bar");
        barRepository.findAll();
        barRepository.findAll();
        sw.stop();

        sw.start("foo");
        fooRepository.findAll();
        fooRepository.findAll();
        sw.stop();

        stopWatches.add(sw);
    }


}
