# Let's Try Test to Spring Data JDBC 


- Object Creation
- [Object Type Mapping](src/test/java/masil/example/springdata/jdbc/MappingObjectTypeTest.java)
- [Mapping Composite Identity to PK](src/test/java/masil/example/springdata/jdbc/MappingCompositeIDToPKTest.java)



## Object Creation

<details><summary>If there’s a no-argument constructor, it will be used. Other constructors will be ignored</summary>
<p>

#### Code
```java
@Table("PRODUCT")
@Getter
public static class TestObject1 {
  @Id
  private Long id;
  private String name;

  @Transient
  private boolean defaultConstructCall = false;

  private TestObject1() {
    defaultConstructCall = true;
  }

  public TestObject1(String name) {
    this.name = name;
  }

}

@Test
@DisplayName("If there’s a no-argument constructor, it will be used. Other constructors will be ignored.")
void object_creation_resolution_algorithm_1() {
  TestObject1 saved = operations.save(new TestObject1("macbook"));
  TestObject1 found = operations.findById(saved.getId(), TestObject1.class);

  assert found != null;

  assertThat(found.isDefaultConstructCall()).isTrue();
  assertThat(found.getName()).isEqualTo("macbook");
}
```
no-argument 생성자가 있을 경우, 다른 생성자는 무시되어 no-argument 생성자를 사용한다.

</p>
</details>

<details><summary>If there’s a single constructor taking arguments, it will be used.</summary>
<p>

#### Code
```java
@Getter
@Table("PRODUCT")
public static class TestObject2 {
  @Id
  private Long id;
  private String name;

  @Transient
  private boolean constructCall = false;

  public TestObject2(String name) {
    this.name = name;
    this.constructCall = true;
  }
}

@Test
@DisplayName("If there’s a single constructor taking arguments, it will be used.")
void object_creation_resolution_algorithm_2() {
  TestObject2 saved = operations.save(new TestObject2("macbook"));
  TestObject2 found = operations.findById(saved.getId(), TestObject2.class);

  assert found != null;

  assertThat(found.isConstructCall()).isTrue();
}
```
단일 생성자가 있을 경우, 단일 생성자를 사용한다.

</p>
</details>

<details><summary>If there are multiple constructors taking arguments, the one to be used by Spring Data will have to be annotated with `@PersistenceConstructor`.</summary>
#### Code
<p>

```java
@Getter
@Table("PRODUCT")
public static class TestObject5 {

  public static TestObject5 of(String name) {
    return new TestObject5(name);
  }

  @Id
  private Long id;
  private String name;

  @Transient
  private boolean constructCall = false;

  @PersistenceConstructor
  private TestObject5(Long id, String name) {
    this.id = id;
    this.name = name;
    this.constructCall = true;
  }

  TestObject5(String name) {
    this(null, name);
  }
}

@Test
void object_creation_resolution_algorithm_4() {
  TestObject5 saved = operations.save(new TestObject5("macbook"));

  TestObject5 found = operations.findById(saved.getId(), TestObject5.class);

  assert found != null;

  assertThat(found.constructCall).isTrue();
}
```
@PersistenceConstructor 애노테이션이 붙은 생성자가 있을 경우, 해당 생성자를 사용한다.

</p>

</details>

<details><summary>Recommend</summary>
<p>

#### Code


```java
@Getter
@Table("PRODUCT")
@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_=@PersistenceConstructor )
// ** Recommend!!!!
public static class TestObject6 {

  public static TestObject5 of(String name) {
    return new TestObject5(name);
  }

  @Id
  private Long id;
  private String name;

  TestObject6(String name) {
    this(null, name);
  }
}
```

</p>
</details>

## Identity

- Generate in a db
    - [Object Type Mapping](src/test/java/masil/example/springdata/jdbc/MappingObjectTypeTest.java)
- Generate in a Client
    - [Mapping Object ID to PK](src/test/java/masil/example/springdata/jdbc/MappingIDToPkTest.java)
    - [Mapping Composite Identity to PK](src/test/java/masil/example/springdata/jdbc/MappingCompositeIDToPKTest.java)
    - [Mapping Composite Identity to Composite Keys]()



## Dependencies 
```groovy
// applying Plugin for using lombok 
id "io.freefair.lombok" version "5.3.3.3"

// build.gradle
implementation 'org.springframework.data:spring-data-jdbc:2.2.0'

// for using hsqldb
runtimeOnly 'org.hsqldb:hsqldb:2.5.2'


// junit5, assertj
testImplementation(platform('org.junit:junit-bom:5.7.1'))
testImplementation('org.junit.jupiter:junit-jupiter')
testImplementation 'org.assertj:assertj-core:3.19.0'
testImplementation 'org.springframework:spring-test:5.3.6'
```



## configuration

​```java

@SpringJUnitConfig(classes = DataJdbcConfiguration.class)
class DataJdbcConfigurationTest {


    @Autowired
    ApplicationContext ctx;

    @Test
    void untitle() {
        Assertions.assertThat(ctx).isNotNull();
    }
}
```

```java

@Configuration
public class DataJdbcConfiguration extends AbstractJdbcConfiguration {


}

```

```log
Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {}
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.raiseNoMatchingBeanFound(DefaultListableBeanFactory.java:1790)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1346) 
```

make schema 
make aggregation
make repository

test 

```composer log
No bean named 'transactionManager' available: No matching TransactionManager bean found for qualifier 'transactionManager' - neither qualifier match nor bean name match!
```


만약 객체를 생성 하기위해 사용될 생성자가 없다면 아래와 같아진다. 
```composer log

Cannot set property id because no setter, wither or copy constructor exists for class masil.example.springdata.jdbc.Product!
```

간단한 aggregation 에 1:N 추가 (set)

