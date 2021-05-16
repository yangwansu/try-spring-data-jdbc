# Let's Try Test to Spring Data JDBC 




## Index

- [Configuration - Not Yet](docs/JdbcConfiguration.md)
- [Object Creation](docs/ObjectCreation.md)
- [Relation Mapping](docs/RelationMapping.md)

- Identity
  - Generate in db
  
    - [Object Type Mapping](docs/ObjectTypeMapping.md)
  - Generate in client
    
    - [Mapping Object ID to PK](docs/CompositeTypeMapping.md#mapping-object-id-to-pk)
    - [Mapping Composite Identity to PK](docs/CompositeTypeMapping.md#mapping-composite-identity-to-pk)
    - [Mapping Composite Identity to Composite Keys - Not Yet](docs/CompositeTypeMapping.md#mapping-composite-identity-to-composite-keys)
  
  
## Dependencies 

```groovy
// build.gradle

// applying Plugin for using lombok 
id "io.freefair.lombok" version "5.3.3.3"

implementation 'org.springframework.data:spring-data-jdbc:2.2.0'

// for using hsqldb
runtimeOnly 'org.hsqldb:hsqldb:2.5.2'


// junit5, assertj
testImplementation(platform('org.junit:junit-bom:5.7.1'))
testImplementation('org.junit.jupiter:junit-jupiter')
testImplementation 'org.assertj:assertj-core:3.19.0'
testImplementation 'org.springframework:spring-test:5.3.6'
```