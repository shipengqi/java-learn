# 单元测试

## JUnit

如果一个文件 `Add.java` 要进行测试，需要写一个 `AddTest.java`，分别放在 `src` 和 `Test` 目录。测试文件以 `Test` 为后缀是一个惯例。

测试方法必须加上 `@Test` 注解，JUnit 才会识别为测试方法。

`org.junit.jupiter.api.Assertions` 类中定义的常用的断言方法：

- `assertEquals(expected, actual)`：期待结果相等
- `assertTrue()`：期待结果为 `true`
- `assertFalse()`：期待结果为 `false`
- `assertNotNull()`：期待结果为非 `null`
- `assertArrayEquals()`：期待结果为数组并与期望数组每个元素的值均相等

## Fixture

- `@BeforeEach` `@AfterEach` 会在运行每个 `@Test` 方法前后自动运行。
  - 对于实例变量，在 `@BeforeEach` 中初始化，在 `@AfterEach` 中清理，它们在各个 `@Test` 方法中互不影响，因为是不同实例。
- `@BeforeAll` `@AfterAll` 会在所有 `@Test` 方法前后仅运行一次。
  - 对于静态变量，在 `@BeforeAll` 中初始化，在 `@AfterAll` 中清理，它们在各个方法中是唯一实例，会影响所有 `@Test` 方法。

## 异常测试

使用 `assertThrows()` 来捕获一个指定的异常。

## 条件测试

在有些时候需要排除一些 `@Test` 方法，使它们不运行，可以添加 `@Disable`：

```java
@Disable
@Test
void testBug() {

}
```

如果要区分在不同系统上运行的测试，使用 `@EnableOnOs`：

```java
@Test
@EnableOnOs(OS.WINDOWS)
void testWin() {}


@Test
@EnableOnOs({OS.LINUX, OS.MAC})
void testLinuxOrMac() {}
```

其他的条件测试：

- `@DisableOnOs(OS.WINDOWS)` 不在 Windows 平台执行的测试
- `@DisabledOnJre(JRE.JAVA_8)` 只能在 Java 9 或更高版本执行的测试
- `@EnabledIfEnvironmentVariable(named = "DEBUG", matches = "true")` 需要传入环境变量 `DEBUG=true` 才能执行的测试
- `@EnabledIfSystemProperty(named = "os.arch", matches = ".*64.*")` 只能在64位操作系统上执行的测试

## 参数化测试

有时候测试方法需要接收至少一个参数，然后，传入一组参数反复运行。可以使用 `@ParameterizedTest`，来进行参数化测试。

```java
@ParameterizedTest
@ValueSouce(ints = {0, 1, 2, 3})
void teseAbs(int x) {
  assertEquals(x, Math.abs(x));
}
```

`@ValueSouce` 传入一组参数。

如果测试方法需要接收两个参数，可以使用 `@MethodSource`，它允许使用一个同名的静态方法来提供参数：

```java
@ParameterizedTest
@MethodSource
void testCapitalize(String input, String result) {
    assertEquals(result, StringUtils.capitalize(input));
}

static List<Arguments> testCapitalize() {
    return List.of( // arguments:
            Arguments.arguments("abc", "Abc"), //
            Arguments.arguments("APPLE", "Apple"), //
            Arguments.arguments("gooD", "Good"));
}
```

静态方法 `testCapitalize()` 返回了一组测试参数，每个参数都包含两个 `String`，正好作为测试方法的两个参数传入。

`@MethodSource` 允许指定方法名。但使用默认同名方法最方便。

另一种传入测试参数的方法是使用 `@CsvSource`，它的每一个字符串表示一行，一行包含的若干参数用 `,` 分隔，下面的代码和上面的效果一样：

```java
@ParameterizedTest
@CsvSource({ "abc, Abc", "APPLE, Apple", "gooD, Good" })
void testCapitalize(String input, String result) {
    assertEquals(result, StringUtils.capitalize(input));
}
```

测试数据很多的情况下，可以使用 `@CsvFileSource`，将测试数据放到一个 `CSV` 文件中：

```java
@ParameterizedTest
@CsvFileSource(resources = { "/test-capitalize.csv" })
void testCapitalizeUsingCsvFile(String input, String result) {
    assertEquals(result, StringUtils.capitalize(input));
}
```

JUnit 只在 `classpath` 中查找指定的 CSV 文件，`test-capitalize.csv` 这个文件要放到 `test` 目录下。
