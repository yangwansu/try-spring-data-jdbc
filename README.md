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


### [9.6.8. Entity State Detection Strategies](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#is-new-state-detection)
 - [VersionTest](src/test/java/masil/example/springdata/jdbc/persisting_entities/VersionTest.java)
### [9.6.10. Optimistic Locking](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.entity-persistence.optimistic-locking)

Aggregate Root 에 @Version Annotation을 사용한 Filed 를 사용해 낙관적 락을 제공한다. 
- [OptimisticLockingTest](src/test/java/masil/example/springdata/jdbc/persisting_entities/OptimisticLockingTest.java)
