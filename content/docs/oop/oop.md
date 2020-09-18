# 面向对象

## 抽象类

如果父类的方法不需要实现，只是为了子类去覆写，可以把父类的方法声明为**抽象方法**。如果声明了抽象方法，那么该类也要声明为**抽象类**，否则无法编译。

```java
abstract class Person {
  public abstract void run();
}
```

使用 `abstract` 关键字类声明抽象类和抽象方法。

**抽象类是不能实例化的，只能用于被继承**。子类必须实现父类定义的抽象方法，否则编译会报错。

### 面向抽象编程

例如定义了抽象类 `Person` 和子类 `Student` 和 `Teacher`，可以使用 `Person` 类来引用具体的子类实例：

```java
Person s = new Student();
Person t = new Teacher();
s.run();
t.run();
```

尽量引用高层类型，避免使用实际子类型的方式，叫做**面向抽象编程**。

- 上层代码只定义规范，如 `abstract class Person`。
- 具体的业务逻辑由各个子类实现，调用者不用关心。
