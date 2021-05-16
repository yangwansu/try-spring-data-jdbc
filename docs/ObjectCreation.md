# Object Creation

[관련 공식문서](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#mapping.object-creation)

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

<br>

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

<br>

<details><summary>If there are multiple constructors taking arguments, the one to be used by Spring Data will have to be annotated with `@PersistenceConstructor`.</summary>
<p>

#### Code


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

<br>

<details><summary>Recommend Object Creation</summary>
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

[처음으로 돌아가기](../README.md)