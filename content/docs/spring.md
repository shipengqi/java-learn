# Spring 开发

Spring 是一个支持 JavaEE 的框架，提供了一系列底层容器和基础设施。

在 Spring Framework 的基础上有诞生了 Spring Boot，Spring Cloud，Spring Data，Spring Security 等项目。

Spring Framework主要包括几个模块：

支持IoC和AOP的容器；
支持JDBC和ORM的数据访问模块；
支持声明式事务的模块；
支持基于Servlet的MVC开发；
支持基于Reactive的Web开发；
以及集成JMS、JavaMail、JMX、缓存等其他模块。

## IoC 容器

IoC 是 Inversion of Control 的缩写，**控制反转**
CartServlet创建了BookService，在创建BookService的过程中，又创建了DataSource组件。这种模式的缺点是，一个组件如果要使用另一个组件，必须先知道如何正确地创建它。

在IoC模式下，控制权发生了反转，即从应用程序转移到了IoC容器，所有组件不再由应用程序自己创建和配置，而是由IoC容器负责，这样，应用程序只需要直接使用已经创建好并且配置好的组件。为了能让组件在IoC容器中被“装配”出来，需要某种“注入”机制，例如，BookService自己并不会创建DataSource，而是等待外部通过setDataSource()方法来注入一个DataSource：

```java
public calss BookService {
  private DataSource datasource;

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }
}
```

这种注入的方式的好处：

1. BookService 不需要关心如何创建 DataSource
2. DataSource 实例被注入到多个实例，共享组件
3. 测试 BookSource 更简单，因为是外部注入 DataSource，可以使用内存数据库，而不是真正的数据库

IoC 又叫做**依赖注入** （Dependency injection），将创建和配置与组件的使用分离，由 IoC 容器负责管理组件的生命周期。

在 Spring 的 IoC 中，所有的组件统称为 JavaBean，即配置一个组件就是配置一个 Bean。

```xml
<beans>
    <bean id="dataSource" class="HikariDataSource" />
    <bean id="bookService" class="BookService">
        <property name="dataSource" ref="dataSource" />
    </bean>
    <bean id="userService" class="UserService">
        <property name="dataSource" ref="dataSource" />
    </bean>
</beans>
```

## 依赖注入方式

依赖注入可以是通过 `setter` 方法实现，也可以通过构造方法实现。

```java
public calss BookService {
  private DataSource dataSource;

  public BookService(DataSource dataSource) {
    this.dataSource = dataSource;
  }
}
```

Spring 同时支持属性注入和构造方法注入，并且可以混合使用。

## 使用 Annotation 配置

使用 xml 配置的优点是搜友的 Bean 一目了然，可以直观的看到每个 Bean 的依赖。缺点是每增加一个组件，都必须修改 xml 文件。

```java
@Component
public class MailService {}
```

`@Component` 注解就相当于定义了一个 Bean，他有一个可选的名称，默认是 `mailService`，小写开头的类名。

```java
@Component
public class UserService {
  @Autowried
  MailService mailService;
}
```

`@Autowried` 就相当于把指定类型的 Bean 注入到指定字段。`@Autowried` 还可以写在 `setter` 方法上或者构造方法中：

```java
@Component
public class UserService {
  MailService mailService;
  
  public UserService(@Autowired MailService mailService) {
    this.mailService = mailService;
  }
}
```

使用Annotation配合自动扫描能大幅简化Spring的配置，我们只需要保证：

每个Bean被标注为`@Component`并正确使用`@Autowired`注入；
配置类被标注为`@Configuration`和`@ComponentScan`；
所有Bean均在指定包以及子包内。

## 定制 Bean

标记为 `@Compoment` 的 Bean 都是单例的，如果每次调用想要返回新的实例需要 `@Scope` 注解，这种 Bean 叫做 Prototype 原型。

```java
@Component
@Scope(ConfigurationBeanFactory.SCOPE_PROTOTYPE)
public class MialSession {}

```

### 注入 List

### 可选注入

### 第三方 Bean

### 初始化和销毁

Spring容器会对上述Bean做如下初始化流程：

调用构造方法创建`MailService`实例；
根据`@Autowired`进行注入；
调用标记有`@PostConstruct`的`init()`方法进行初始化。
而销毁时，容器会首先调用标记有`@PreDestroy`的`shutdown()`方法。

### 使用别名

### FactoryBean

## Resource

## 注入配置

Spring容器还提供了一个更简单的`@PropertySource`来自动读取配置文件。我们只需要在`@Configuration`配置类上再添加一个注解：

```java
@Configuration
@ComponentScan
@PropertySource("app.properties") // 表示读取classpath的app.properties
public class AppConfig {
    @Value("${app.zone:Z}")
    String zoneId;

    @Bean
    ZoneId createZoneId() {
        return ZoneId.of(zoneId);
    }
}
```

`"${app.zone}"`表示读取key为`app.zone`的value，如果key不存在，启动将报错；
`"${app.zone:Z}"`表示读取key为`app.zone`的value，但如果key不存在，就使用默认值`Z`。

## 条件装配

`@Profile` 可以用来表示不同的环境。如开发，测试，生产三个环境：`native` `test` `production`。

创建某个 Bean 时，Spring 容器可以根据 `@Profile` 来决定是否创建：

```java
@Configuration
@ComponentScan
public class AppConfig {
    @Bean
    @Profile("!test")
    ZoneId createZoneId() {
        return ZoneId.systemDefault();
    }

    @Bean
    @Profile("test")
    ZoneId createZoneIdForTest() {
        return ZoneId.of("America/New_York");
    }  
}
```

运行程序时，JVM 参数 `-Dspring.profiles.active=test` 就可以指定测试环境。

`-Dspring.profiles.active=test,master`

```java
@Bean
@Profile({ "test", "master" }) // 同时满足test和master
ZoneId createZoneId() {
    ...
}
```

### Conditional

```java
@Component
@Conditional(OnSmtpEnvCondition.class)
public class SmtpMailService implements MailService {
    ...
}

public class OnSmtpEnvCondition implements Condition {
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return "true".equalsIgnoreCase(System.getenv("smtp"));
    }
}
```

`@Conditional`决定是否创建某个Bean
如果满足`OnSmtpEnvCondition`的条件，才会创建`SmtpMailService`这个Bean

## AOP

AOP 是对 OOP 的补充和完善。OOP引入封装、继承和多态性等概念来建立一种对象层次结构，用以模拟公共行为的一个集合。当我们需要为分散的对象引入公共行为的时候，OOP则显得无能为力。例如日志功能。日志代码往往水平地散布在所有对象层次中，而与它所散布到的对象的核心功能毫无关系。这种散布在各处的无关的代码被称为横切（cross-cutting）代码，在OOP设计中，它导致了大量代码的重复，而不利于各个模块的重用。

AOP 利用一种称为“横切”的技术，剖解开封装的对象内部，并将那些影响了多个类的公共行为封装到一个可重用模块，并将其名为“Aspect”，即切面。所谓“切面”，简单地说，就是将那些与业务无关，却为业务模块所共同调用的逻辑或责任封装起来，便于减少系统的重复代码，降低模块间的耦合度，并有利于未来的可操作性和可维护性。

AOP 的植入方式有三种：

1. 编译期，由编译器把切面调用编译到字节码，这种方式需要扩展编译器，AspectJ 就扩展了编译器，使用关键字 aspect 来实现织入。
2. 类加载器：在目标类被装载到 JVM 时，通过一个特殊的类加载器，对目标类的字节码重新增强。
3. 运行期，目标对象和切面都是普通的 Java 类，通过动态代理功能或者第三方库实现运行期织入。

Spring 的 AOP 实现基于第三种方式。JVM 的动态逮了要求必须实现接口。

AOP 本质上就是一个代理模式。

## AOP 装配

AOP 装配的最好的方式是使用注解。

## 数据库访问

Spring为了简化数据库访问，主要做了以下几点工作：

提供了简化的访问JDBC的模板类，不必手动释放资源；
提供了一个统一的DAO类以实现Data Access Object模式；
把SQLException封装为DataAccessException，这个异常是一个RuntimeException，并且让我们能区分SQL异常的原因
能方便地集成Hibernate、JPA和MyBatis这些数据库访问框架。
