# ObjectTypeMapping

[관련 공식문서](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.entity-persistence)

**SQL Schema**
```sql
CREATE TABLE IF NOT EXISTS TEST_TABLE (id INTEGER IDENTITY PRIMARY KEY , name varchar(100), score INTEGER , createdAt bigint)
```

**JdbcConfiguration**

```java
@Configuration
public static class Config extends AbstractJdbcConfiguration {

    @Bean
    DataSource dataSource() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).addScript("MappingObjectTypeTest.sql").build();
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
        return new JdbcCustomConversions(Arrays.asList(new IntegerToTestEntityIdConvertor(), new TestEntityIdToIntegerConvertor()));
    }

}
```

특정 타입을 맵핑하기 위해서는 아래와 같은 Convertor 를 만들어야 한다.

```java
@ReadingConverter
public static class IntegerToTestEntityIdConvertor implements Converter<Integer, TestEntityId> {

	@Override
	public TestEntityId convert(Integer source) {
		return TestEntityId.of(OtherAggregationRef.of(source));
	}
}

@WritingConverter
public static class TestEntityIdToIntegerConvertor implements Converter<TestEntityId,Integer> {

	@Override
	public Integer convert(TestEntityId source) {
		return source.getValue().getId();
	}
}
```

```java
// Test Code
@Test
@DisplayName("Object Type ID Mapping ")
void objectTypeId() {

    TestEntity saved = aggregateOperations.save(TestEntity.of("xxx"));

    TestEntity find = aggregateOperations.findById(saved.getId(), TestEntity.class);

    assert find != null;

    assertThat(find.getId()).isEqualTo(saved.getId());

}
```

[처음으로 돌아가기](../README.md)