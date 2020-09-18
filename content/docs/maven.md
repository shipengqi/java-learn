# Maven

Maven 是 Java 项目的管理和构建工具，包括依赖管理，项目构建等功能。Maven本质上是一个插件框架，并不执行任何具体的构建任务，它把所有这些任务都交给插件来完成。

一般 maven 管理的项目结构：

```bash
a-maven-project
├── pom.xml
├── src
│   ├── main
│   │   ├── java      # 源码
│   │   └── resources # 存放资源文件
│   └── test
│       ├── java      # 测试源码
│       └── resources # 存放测试资源文件
└── target
```

pom.xml：

```xml
<project ...>
 <modelVersion>4.0.0</modelVersion>
 <groupId>com.itranswarp.learnjava</groupId>
 <artifactId>hello</artifactId>
 <version>1.0</version>
 <packaging>jar</packaging>
 <properties>
        ...
 </properties>
 <dependencies>
        <dependency>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
            <version>1.2</version>
        </dependency>
 </dependencies>
</project>
```

groupId类似于Java的包名，通常是公司或组织名称，artifactId类似于Java的类名，通常是项目名称，再加上version，一个Maven工程就是由groupId，artifactId和version作为唯一标识。

使用 `<dependency>` 声明一个依赖后，Maven就会自动下载这个依赖包并把它放到classpath中。

## 依赖管理

依赖关系：

- `compile` 默认值，编译时需要用的 jar 包，Maven 会把这种类型的依赖直接放入 classpath。
- `test` 编译 Test 时用的 jar 包
- `runtime` 编译时不需要，运行时需要用的
- `provided` 编译时需要用到，运行时由 JDK 或某个服务器提供

最常用的 test 依赖就是 JUnit：

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.3.2</version>
    <scope>test</scope>
</dependency>
```

最典型的 runtime 依赖是 JDBC 驱动，例如 MySQL 驱动：

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.48</version>
    <scope>runtime</scope>
</dependency>
```

最典型的provided依赖是Servlet API，编译的时候需要，但是运行时，Servlet服务器内置了相关的jar，所以运行期不需要：

```java
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>4.0.0</version>
    <scope>provided</scope>
</dependency>
```

Maven维护了一个[中央仓库](https://repo1.maven.org/)，所有第三方库将自身的jar以及相关信息上传至中央仓库，Maven就可以从中央仓库把所需依赖下载到本地。

Maven并不会每次都从中央仓库下载jar包。一个jar包一旦被下载过，就会被Maven自动缓存在本地目录（用户主目录的 `.m2` 目录）

Maven通过对jar包进行PGP签名确保任何一个jar包一经发布就无法修改。修改已发布jar包的唯一方法是发布一个新版本。

只有以 `-SNAPSHOT` 结尾的版本号会被Maven视为开发版本，开发版本每次都会重复下载，这种SNAPSHOT版本只能用于内部私有的Maven repo，公开发布的版本不允许出现SNAPSHOT。

使用Maven镜像仓库需要一个配置，在用户主目录下进入 `.m2` 目录，创建一个 `settings.xml` 配置文件，内容如下：

```xml
<settings>
    <mirrors>
        <mirror>
            <id>aliyun</id>
            <name>aliyun</name>
            <mirrorOf>central</mirrorOf>
            <!-- 国内推荐阿里云的Maven镜像 -->
            <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
        </mirror>
    </mirrors>
</settings>
```

搜索第三方组件使用 [search.maven.org](https://search.maven.org/)

## 命令行

编译：在 `pom.xml` 所在的目录执行 `mvn clean package`，会在 `target` 目录下生成 jar 包。

## 构建流程

maven 的生命周期，包含一系列 phase，例如，内置的 `default` 生命周期包含的 phase：

```bash
validate
initialize
generate-sources
process-sources
generate-resources
process-resources
compile
process-classes
generate-test-sources
process-test-sources
generate-test-resources
process-test-resources
test-compile
process-test-classes
test
prepare-package
package
pre-integration-test
integration-test
post-integration-test
verify
install
deploy
```

`mvn package` 命令会从生命周期的开始运行到 `package` pahase。
`mvn complie` 命令会从生命周期的开始运行到 `complie` pahase。

`clean` 生命周期会运行三个 phase：

```bash
pre-clean
clean （注意这个 clean 不是 lifecycle 而是 phase）
post-clean
```

`mvn` 命令后面的参数是 phase，Maven自动根据生命周期运行到指定的phase。例如运行 `mvn clean package` 会先执行 clean 生命周期并运行到clean这个phase，然后执行default生命周期并运行到package这个phase，实际执行的phase如下：

```bash
pre-clean
clean （注意这个clean是phase）
validate
...
package
```

经常使用的命令有：

- `mvn clean`：清理所有生成的class和jar；
- `mvn clean compile`：先清理，再执行到compile；
- `mvn clean test`：先清理，再执行到test，因为执行test前必须执行compile，所以这里不必指定compile；
- `mvn clean package`：先清理，再执行到package。

执行一个phase又会触发一个或多个goal，goal的命名总是 `abc:xyz` 这种形式。

- lifecycle相当于Java的package，它包含一个或多个phase；
- phase相当于Java的class，它包含一个或多个goal；
- goal相当于class的method，它其实才是真正干活的。

只有少数情况，我们可以直接指定运行一个goal，例如，启动Tomcat服务器：`mvn tomcat:run`

通常情况，我们总是执行phase默认绑定的goal，因此不必指定goal。

## 插件

Maven将执行compile这个phase，这个phase会调用compiler插件执行关联的 `compiler:compile` 这个goal。

实际上，执行每个phase，都是通过某个插件（plugin）来执行的，Maven本身其实并不知道如何执行compile，它只是负责找到对应的compiler插件，然后执行默认的`compiler:compile` 这个goal来完成编译。

Maven已经内置了一些常用的标准插件：

插件名称 对应执行的phase
clean clean
compiler compile
surefire test
jar package

还可以使用自定义插件，例如，使用maven-shade-plugin可以创建一个可执行的jar，要使用这个插件，需要在pom.xml中声明它：

```xml
<project>
    ...
 <build>
  <plugins>
   <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
                <version>3.2.1</version>
    <executions>
     <execution>
      <phase>package</phase>
      <goals>
       <goal>shade</goal>
      </goals>
      <configuration>
                            ...
      </configuration>
     </execution>
    </executions>
   </plugin>
  </plugins>
 </build>
</project>
```

自定义插件往往需要一些配置，例如，maven-shade-plugin需要指定Java程序的入口，它的配置是：

```xml
<configuration>
    <transformers>
        <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
            <mainClass>com.itranswarp.learnjava.Main</mainClass>
        </transformer>
    </transformers>
</configuration>
```

Maven自带的标准插件例如compiler是无需声明的，只有引入其它的插件才需要声明。

一些常用的插件：

maven-shade-plugin：打包所有依赖包并生成可执行jar；
cobertura-maven-plugin：生成单元测试覆盖率报告；
findbugs-maven-plugin：对Java源码进行静态分析以找出潜在问题。

## 模块管理

大项目可以拆分出对个模块

multiple-project
├── pom.xml
├── parent
│   └── pom.xml
├── module-a
│   ├── pom.xml
│   └── src
├── module-b
│   ├── pom.xml
│   └── src
└── module-c
    ├── pom.xml
    └── src

Maven可以有效地管理多个模块，只需要把每个模块当作一个独立的Maven项目，它们有各自独立的 `pom.xml`

## mvnw

mvnw是Maven Wrapper的缩写,对于某些项目来说，它可能必须使用某个特定的Maven版本，这个时候，就可以使用Maven Wrapper，它可以负责给这个特定的项目安装指定版本的Maven

安装Maven Wrapper：`mvn -N io.takari:maven:0.7.6:wrapper`

如果要指定使用的Maven版本，使用下面的安装命令指定版本，例如3.3.3：`mvn -N io.takari:maven:0.7.6:wrapper -Dmaven=3.3.3`

只需要把mvn命令改成mvnw就可以使用跟项目关联的Maven

Maven Wrapper的另一个作用是把项目的mvnw、mvnw.cmd和.mvn提交到版本库中，可以使所有开发人员使用统一的Maven版本。
