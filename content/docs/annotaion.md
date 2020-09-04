# Annotation
Annotation （注解）是放在类，方法，字段，参数钱的一种注释：
```java

```

注解是一种用作标注的“元数据”。

## 定义注解

定义注解使用 `@interface`：
```java
public @interface Report {
    int type() default 0;
    String level() default "info";
    String value() default "";
}
```


### 元注解
元注解可以修饰其他注解。Java 定义了一些元注解。

#### `@Target`

`@Target` 用来定义注解能够被应用于源码的哪些位置：
- 类和接口：`ElementType.TYPE`
- 字段：`ElementType.FIELD`
- 方法：`ElementType.METHOD`
- 构造方法：`ElementType.CONSTRUCTOR`
- 方法参数：`ElementType.PARAMETER`


```java
@Target(ElementType.METHOD)
public @interface Report {
    int type() default 0;
    String level() default "info";
    String value() default "";
}
```

上面的示例，Report 注解只能用在方法上。

#### `@Retention`

`@Retention` 定义了注解的生命周期。

- 仅编译期：`RetentionPolicy.SOURCE`
- 仅 class 文件：`RetentionPolicy.CLASS`
- 运行期：`RetentionPolicy.RUNTIME`

通常我们自定义的注解都是 `RUNTIME`：
```java
@Retention(RetentionPolicy.RUNTIME)
public @interface Report {
    int type() default 0;
    String level() default "info";
    String value() default "";
}
```
#### `@Repeatable`

`@Repeatable` 定义注解是否可以重复。

#### `@Inherited`

`@Inherited` 定义子类是否可以继承父类的注解。`@Inherited` 仅针对 `@Target(ElementType.TYPE)` 类型的注解有效，并且仅针对 class 的继承，对 interface 的继承无效：


## 处理注解
注解定义后也是一种 class，所有的注解都继承自 `java.lang.annotation.Annotation`，因此，读取注解，需要使用反射 API。
