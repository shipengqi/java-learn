# Web 开发

JavaEE 是 Java Platform Enterprise Edition 的缩写，完全基于 JavaSE，只是多了一对关于服务器相关的库和 API 接口。
JavaEE 最核心的组件就是基于 Servlet 标准的 Web 服务器，应用程序基于 Servlet API 并运行在服务器内部。

```bash
┌─────────────┐
│┌───────────┐│
││ User App  ││
│├───────────┤│
││Servlet API││
│└───────────┘│
│ Web Server  │
├─────────────┤
│   JavaSE    │
└─────────────┘
```

## Servlet

JavaEE平台上，处理TCP连接，解析HTTP协议这些底层工作统统扔给现成的Web服务器去做，我们只需要把自己的应用程序跑在Web服务器上。为了实现这一目的，JavaEE提供了Servlet API，我们使用Servlet API编写自己的Servlet来处理HTTP请求，Web服务器实现Servlet API接口，实现底层功能：

```bash
                 ┌───────────┐
                 │My Servlet │
                 ├───────────┤
                 │Servlet API│
┌───────┐  HTTP  ├───────────┤
│Browser│<──────>│Web Server │
└───────┘        └───────────┘
```

```java
// WebServlet注解表示这是一个Servlet，并映射到地址/:
@WebServlet(urlPatterns = "/")
public class HelloServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 设置响应类型:
        resp.setContentType("text/html");
        // 获取输出流:
        PrintWriter pw = resp.getWriter();
        // 写入响应:
        pw.write("<h1>Hello, world!</h1>");
        // 最后不要忘记flush强制输出:
        pw.flush();
    }
}
```

Servlet API是一个jar包，我们需要通过Maven来引入它

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.itranswarp.learnjava</groupId>
    <artifactId>web-servlet-hello</artifactId>
    <packaging>war</packaging>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <java.version>11</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>4.0.0</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <build>
        <finalName>hello</finalName>
    </build>
</project>
```

打包类型不是jar，而是war
注意到`<scope>`指定为provided，表示编译时使用，但不会打包到.war文件中，因为运行期Web服务器本身已经提供了Servlet API相关的jar包。

还需要在工程目录下创建一个web.xml描述文件，放到`src/main/webapp/WEB-INF`目录下

```xml
<!DOCTYPE web-app PUBLIC
 "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
 "http://java.sun.com/dtd/web-app_2_3.dtd">
<web-app>
  <display-name>Archetype Created Web Application</display-name>
</web-app>
```

工程结构如下：

```bash
web-servlet-hello
├── pom.xml
└── src
    └── main
        ├── java
        │   └── com
        │       └── itranswarp
        │           └── learnjava
        │               └── servlet
        │                   └── HelloServlet.java
        ├── resources
        └── webapp
            └── WEB-INF
                └── web.xml
```

行Maven命令mvn clean package，在target目录下得到一个hello.war文件，这个文件就是我们编译打包后的Web应用程序。

Web应用程序有所不同，我们无法直接运行war文件，必须先启动Web服务器，再由Web服务器加载我们编写的HelloServlet，这样就可以让HelloServlet处理浏览器发送的请求。

首先要找一个支持Servlet API的Web服务器。常用的服务器有：

Tomcat：由Apache开发的开源免费服务器；
Jetty：由Eclipse开发的开源免费服务器；
GlassFish：一个开源的全功能JavaEE服务器。

只要它支持Servlet API 4.0（因为我们引入的Servlet版本是4.0），我们的war包都可以在上面运行。使用最广泛的开源免费的Tomcat服务器。

把hello.war复制到Tomcat的webapps目录下，然后切换到bin目录，执行startup.sh或startup.bat启动Tomcat服务器：

```bash
./startup.sh
Using CATALINA_BASE:   .../apache-tomcat-9.0.30
Using CATALINA_HOME:   .../apache-tomcat-9.0.30
Using CATALINA_TMPDIR: .../apache-tomcat-9.0.30/temp
Using JRE_HOME:        .../jdk-11.jdk/Contents/Home
Using CLASSPATH:       .../apache-tomcat-9.0.30/bin/bootstrap.jar:...
Tomcat started.
```

在浏览器输入`http://localhost:8080/hello/`即可看到HelloServlet的输出

为啥路径是 `/hello/` 而不是 `/`？

因为一个Web服务器允许同时运行多个Web App，而我们的Web App叫hello，因此，第一级目录`/hello`表示Web App的名字，后面的`/`才是我们在HelloServlet中映射的路径。

关闭Tomcat（执行shutdown.sh或shutdown.bat），然后删除Tomcat的webapps目录下的所有文件夹和文件，最后把我们的hello.war复制过来，改名为ROOT.war，文件名为ROOT的应用程序将作为默认应用，启动后直接访问`http://localhost:8080/`即可。

## IDE中启动并调试webapp的方法

Tomcat实际上也是一个Java程序，我们看看Tomcat的启动流程：

1. 启动JVM并执行Tomcat的`main()`方法；
2. 加载war并初始化Servlet；
3. 正常服务。

完全可以把Tomcat的jar包全部引入进来，然后自己编写一个`main()`方法，先启动Tomcat，然后让它加载我们的webapp就行。

一个Web App就是由一个或多个Servlet组成的，每个Servlet通过注解说明自己能处理的路径。例如：

```java
@WebServlet(urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {
    ...
}
```

浏览器发送请求的时候，还会有请求方法（HTTP Method）：即GET、POST、PUT等不同类型的请求。因此，要处理GET请求，我们要覆写doGet()方法,要处理POST请求，就需要覆写doPost()方法。

## HttpServletRequest

## HttpServletResponse

## 重定向

重定向有两种：一种是302响应，称为临时重定向，一种是301响应，称为永久重定向。两者的区别是，如果服务器发送301永久重定向响应，浏览器会缓存/hi到/hello这个重定向的关联，下次请求/hi的时候，浏览器就直接发送/hello请求了。

HttpServletResponse提供了快捷的redirect()方法实现302重定向。如果要实现301永久重定向，可以这么写：

```java
resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY); // 301
resp.setHeader("Location", "/hello");
```

## Forward

## Session 和 Cookie

## JSP

JSP是Java Server Pages的缩写，它的文件必须放到`/src/main/webapp`下，文件名必须以`.jsp`结尾

可见JSP本质上就是一个Servlet，只不过无需配置映射路径，Web Server会根据路径查找对应的.jsp文件，如果找到了，就自动编译成Servlet再执行。在服务器运行过程中，如果修改了JSP的内容，那么服务器会自动重新编译。

## MVC

Servlet 和 JSP 的 MVC 模式并不是很友好，因为 Servlet 的接口偏底层，JSP 的页面开发更好的替代品是模版引擎，业务代码最好是纯粹的 Java 实现，而不是继承自 Servlet。

## Filter

可以把 Servlet 中的公用逻辑抽离出来，放到 Filter 过滤器中。如 `@WebFilter("/*")`

HttpServletRequest进行读取时，只能读取一次。如果Filter调用 `getInputStream()` 读取了一次数据，后续Servlet处理时，再次读取，将无法读到任何数据。

## Listener

Listener 就是监听器，最常用的是 `ServletContextListener`

```java
@WebListener
public class AppListener implements ServletContextListener {
  public void contextInitialized(ServletContextEvent sce) {
    System.out.println("WebApp initizlized")
  }

public void contextDestroyed(ServletContextEvent sce) {
    System.out.println("WebApp destroyed")
  }
}
```

任何标注为 `@WebListener` 且实现了特定接口的类会被 Web 服务器自动初始化。上面的示例 `AppListener` 实现了 `ServletContextListener` 接口，会在整个应用程序初始化完成后，以及应用程序关闭后获得的回调通知。可以把初始化数据库连接池等工作放到 `contextInitialized()` 回调方法中，把清理资源的工作放到 `contextDestroyed()` 回调方法中，因为 Web 服务器保证在 `contextInitialized()` 执行后，才会接收 HTTP 请求。

## SpingMVC

## SpringBoot

SpringBoot 是基于 Spring 的套件，它组装了一系列 Spring 组件，以便尽可能少的代码和配置来开发基于 Spring 的应用程序。
