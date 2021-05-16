# Spring Data JDBC Configuration

[관련 공식문서](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.java-config)

Spring Data JDBC 사용시 Configuration 을 설정할 수 있다.

각 설정에 테스트는 아래와 같다.


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

위와 같이 설정시 아래와 같은 Exception이 발생한다.
```text
Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {}
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.raiseNoMatchingBeanFound(DefaultListableBeanFactory.java:1790)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1346) 

```


## configuration


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
Cannot set property id because no setter, wither or copy constructor exists for class masil.example.springdata.jdbc.Product!



[처음으로 돌아가기](../README.md)