# Let's Try Test to Spring Data JDBC 

## [8.3. Defining Repository Interfaces](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#repositories.definition)

### [8.3.1. Fine-tuning Repository Definition](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#repositories)

  - [@RepositoryDefinition](src/test/java/masil/example/springdata/jdbc/ch8_3_1/RepositoryDefinitionTest.java)
      Spring Data 에서 제공해주는 CrudRepository 를 상속 받고 싶지 않을 때 메서드들을 재 생성 할 수 있다. delete 를 노출 시키고 싶지 않을 때 유용 할 수 있다.

## [8.7. Publishing Events from Aggregate Roots](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#core.extensions)
  

## [9.6. Persisting Entities](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.entity-persistence)

## [9.6.1. Object Mapping Fundamentals](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#mapping.fundamentals)

**Object creation**

Spring Data JDBC 는 객체를 구체화 하기 위해 Persistence Entity의  생성자를 감지하려 노력합니다.

[Resolution Algorithm Test](src/test/java/masil/example/springdata/jdbc/ch9_6_1/ObjectCreationTest.java)

[Construct-Only Performance](src/test/java/masil/example/springdata/jdbc/ch9_6_1/ConstructorOnlyPerformanceTest.java)
  

### [9.6.7. Embedded entities](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.entity-persistence.embedded-entities)
Embedded Entity 는 테이블이 하나인 경우에도 Data Model에 Value Object를 포함 시키는데 유용하다.

- [OnEmpty Test](src/test/java/masil/example/springdata/jdbc/ch9_6_7/EmbeddedOptionTest.java)

### [9.6.8. Entity State Detection Strategies](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#is-new-state-detection)

Entity 의 상태가 새로운 객체(New)인지 확인 하는 옵션들  
 - [@Version - properties inspection Test](src/test/java/masil/example/springdata/jdbc/ch9_6_8/VersionPropertiesInspectionTest.java)
 - [The Version is always updated on save](src/test/java/masil/example/springdata/jdbc/ch9_6_8/VersionUpdateTest.java)

### [9.6.10. Optimistic Locking](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.entity-persistence.optimistic-locking)

Aggregate Root 에 @Version Annotation을 사용한 Filed 를 사용해 낙관적 락을 제공한다. 
- [OptimisticLockingTest](src/test/java/masil/example/springdata/jdbc/ch9_6_10/OptimisticLockingTest.java)

### [9.7.3. Named Queries](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.query-methods.named-query)

- [Modifying Query](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.query-methods.at-query.modifying)

## [9.12 Logging](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.logging)
  Spring Data Jdbc 는 거의 로깅을 하지 않는다. 로깅을 원한다면 JdbcTemplate의 로그를 활성화하는 것을 고려 할 수 있다.
  - [log4j.properties](src/main/resources/log4j.properties)

## [9.14. Auditing](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#auditing)

### [9.14.1. Basics](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#auditing.basics)

 - [Annotation-based Auditing Metadata Test](src/test/java/masil/example/springdata/jdbc/ch9_14_1/AnnotationBasedAuditingMetadataTest.java)

  - Stackoverflow
    - [In Spring Data JDBC bypass @CreatedDate and @LastModifiedDate](https://stackoverflow.com/questions/67775557/in-spring-data-jdbc-bypass-createddate-and-lastmodifieddate)
      - [Test to Set a field Manually](src/test/java/masil/example/springdata/jdbc/ch9_14_1/ManuallySetupTest.java)


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
