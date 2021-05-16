# Let's Try Test to Spring Data JDBC 


- [Object Creation](docs/ObjectCreation.md)
- [Relation Mapping](docs/RelationMapping.md)
- [Object Type Mapping](src/test/java/masil/example/springdata/jdbc/MappingObjectTypeTest.java)
- [Mapping Composite Identity to PK](src/test/java/masil/example/springdata/jdbc/MappingCompositeIDToPKTest.java)

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

```java

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

​```log
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

