# CompositeTypeMapping

[관련 공식문서](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.entity-persistence)

## Mapping Object ID to PK

**SQL Schema**
```sql
CREATE TABLE IF NOT EXISTS TEST_TABLE (id BIGINT PRIMARY KEY, name varchar(100))
```

JDBC Configuration
```java
@Configuration
public static class Config extends AbstractJdbcConfiguration {
    @Bean
    DataSource dataSource() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).addScript("MappingIDToPkTest.sql").build();
    }
    @Bean
    NamedParameterJdbcOperations jdbcOperations(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
    @Bean
    TransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @Override
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(Arrays.asList(new LongToTestEntityIdConvertor(), new TestEntityIdToLongConvertor()));
    }
}
```

Entity 와 Converter

```java
@Getter
@Table("TEST_TABLE")
@AllArgsConstructor(access = PRIVATE, staticName = "of") //in new
@RequiredArgsConstructor(access = PRIVATE, onConstructor_=@PersistenceConstructor) // in select isNew is false
public static class TestEntity implements Persistable<TestEntityId> {

	public static TestEntity of(TestEntityId id, String name) {
		boolean isNew = true;
		return new TestEntity(id, name, isNew);
	}

	@Id
	private final TestEntityId id;
	private final String name;

	@Transient
	private boolean isNew = false;

	@Override
	public boolean isNew() {
		return isNew;
	}
}

@Value(staticConstructor = "of")
public static class TestEntityId {
	Long id;
}


@ReadingConverter
public static class LongToTestEntityIdConvertor implements Converter<Long, TestEntityId> {
	@Override
	public TestEntityId convert(Long source) {
		return TestEntityId.of(source);
	}
}

@WritingConverter
public static class TestEntityIdToLongConvertor implements Converter<TestEntityId,Long> {
	@Override
	public Long convert(TestEntityId source) {
		return source.getId();
	}
}

```

**테스트 코드**

```java
@Test
    @DisplayName("")
    void name() {
        TestEntity entity = TestEntity.of(TestEntityId.of(1L), "foo");
        assertThat(entity.isNew()).isTrue();
        aggregateOperations.save(entity);
        //aggregateOperations.save(entity);

        TestEntity find = aggregateOperations.findById(entity.getId(), TestEntity.class);
        assertThat(find.isNew()).isFalse();
    }
```

## Mapping Composite Identity to PK

**SQL Schema**
```sql
CREATE TABLE IF NOT EXISTS TEST_TABLE1 (id varchar(100) primary key, name varchar(100))
```

Entity 와 Converter

```java
    @Getter
@Table("TEST_TABLE1")
public static class TestEntity implements Persistable<CompositeKey> {

	public static TestEntity of(CompositeKey id, String name) {
		return new TestEntity(id, name, true);
	}

	@Id
	final CompositeKey id;

	final String name;

	@With
	@Transient
	final Boolean isNew;

	@PersistenceConstructor
	private  TestEntity(CompositeKey id, String name) {
		this(id, name, false); //for read from db
	}

	private TestEntity(CompositeKey id, String name, Boolean isNew) {
		this.id = id;
		this.name = name;
		this.isNew = isNew;
	}

	@Override
	public boolean isNew() {
		return isNew;
	}
}


@Value(staticConstructor = "of")
public static class CompositeKey {
	Long key1;
	String key2;
}

@ReadingConverter
enum StringToCompositeKey implements Converter<String, CompositeKey> {
	INSTANCE;
	@Override
	public CompositeKey convert(String source) {
		String[] split = source.split("::");
		return CompositeKey.of(Long.parseLong(split[0]), split[1]);
	}
}

@WritingConverter
enum CompositeKeyToString implements Converter<CompositeKey, String>{
	INSTANCE;
	@Override
	public String convert(CompositeKey source) {
		return source.key1+"::"+source.getKey2();
	}
}


```

**테스트 코드**

```java
@Test
@DisplayName("Mapping Composite Identity to a PK")
void map_to_pk() {
    TestEntity entity = TestEntity.of(CompositeKey.of(1L,"Wansu"), "foo");
    jdbcAggregateOperations.save(entity);

    assert entity.getId() != null;

    TestEntity find = jdbcAggregateOperations.findById(entity.getId(), TestEntity.class);

assertThat(entity.getId()).isEqualTo(find.getId());
}
```



## Mapping Composite Identity to Composite Keys

// TODO 

```

[처음으로 돌아가기](../README.md)