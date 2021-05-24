# Let's Try Test to Spring Data JDBC 


## Identity




- Generate in a db
    - [Object Type Mapping~](src/test/java/masil/example/springdata/jdbc/identity/MappingObjectTypeTest.java)
- Generate in a Client
    - [Mapping Object ID to PK](src/test/java/masil/example/springdata/jdbc/identity/MappingIDToPkTest.java)
    - [Mapping Composite Identity to PK](src/test/java/masil/example/springdata/jdbc/identity/MappingCompositeIDToPKTest.java)
    - [Mapping Composite Identity to Composite Keys](src/test/java/masil/example/springdata/jdbc/identity/MappingCompositeIDToCompositeKeysTest.java)
    - [Mapping Composite Identity With Surrogate Keys](src/test/java/masil/example/springdata/jdbc/identity/MappingCompositeIDWithSurrogateKeys.java)

    - [BeforeSaveCallback 사용](src/test/java/masil/example/springdata/jdbc/identity/from_client/UsingBeforeSaveCallbackTest.java)
    - [BeforeSaveEvent 사용](src/test/java/masil/example/springdata/jdbc/identity/from_client/UsingBeforeSaveEventTest.java)
    - [Fragment Interface 사용](src/test/java/masil/example/springdata/jdbc/identity/from_client/UsingFragmentsInterfaceTest.java)
    - [Persistable 사용](src/test/java/masil/example/springdata/jdbc/identity/from_client/UsingPersistableTest.java)
    - [@Version을 사용](src/test/java/masil/example/springdata/jdbc/identity/from_client/UsingVersionTest.java)
## Persisting Entities
  - [Construct-Only Performance](src/test/java/masil/example/springdata/jdbc/persisting_entities/ConstructorOnlyPerformanceTest.java)



## [9.6. Persisting Entities](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.entity-persistence)

### [9.6.7. Embedded entities](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.entity-persistence.embedded-entities)
Embedded Entity 는 테이블이 하나인 경우에도 Data Model에 Value Object를 포함 시키는데 유용하다.

- [OnEmpty Test](src/test/java/masil/example/springdata/jdbc/persisting_entities/embedded_entities/OptionTest.java)

### [9.6.8. Entity State Detection Strategies](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#is-new-state-detection)

Entity 의 상태가 새로운 객체(New)인지 확인 하는 옵션들  
 - [@Version - properties inspection Test](src/test/java/masil/example/springdata/jdbc/persisting_entities/VersionPropertiesInspectionTest.java)

### [9.6.10. Optimistic Locking](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.entity-persistence.optimistic-locking)

Aggregate Root 에 @Version Annotation을 사용한 Filed 를 사용해 낙관적 락을 제공한다. 
- [OptimisticLockingTest](src/test/java/masil/example/springdata/jdbc/persisting_entities/OptimisticLockingTest.java)

### [9.7.3. Named Queries](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.query-methods.named-query)

- [Modifying Query](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.query-methods.at-query.modifying)