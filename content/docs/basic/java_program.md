# 程序基本结构

```java
class Hello {

}
```

一个 `.java` 文件只能包含一个 `public` 类，但可以包含多个非 `public` 类。如果有 `public` 类，文件名必须和 `public` 类的名字相同。

## classpath

`classpath` 是 JVM 的一个环境变量，**指示 JVM 如何搜索 `class`**。

源码文件是 `.java`，编译后的 `.class` 文件才可以被 JVM 执行。

`classpath` 是一组目录集合。使用 `:` 分隔，windows 使用 `;` 分隔：

```bash
# linux
/usr/shared:/usr/local/bin:/home/test/bin

# windows
C:\work\project1\bin;C:\shared;"D:\My Documents\project1\bin"
```

### 如何设置 classpath

设置系统环境变量
启动 JVM 时设置 classpath，`-classpath` 或 `-cp` 参数

默认的 classpath 是 `.`，

## jar 包

jar 包就是把分散在多个目录下的 `.class` 文件，打包成一个文件。

jar 包实际上是一个 zip 的压缩文件。要执行 jar 包，可以通过参数 `-cp` 把 jar 包加到 `classpath` 中：

```bash
java -cp ./hello.jar abc.xyz.Hello
```

### 创建 jar 包

先把目录打包到 zip 文件，然后修改后缀 `.zip` 为 `.jar` 就可以了。

jar 包的第一层目录不能是 `bin`。否则 JVM 无法从jar包中找到正确的 `class`，原因是 `hong.Person` 必须按 `hong/Person.class`存放，而不是`bin/hong/Person.class`。

### MANIFEST

jar 包可以包含一个 `/META-INF/MANIFEST.MF` 的文本文件，它可以指定 `Main-Class` 和其他信息。JVM 会自动读取这个文件，启动时，就不需要再指定类名：

```bash
java -jar hello.jar
```

大型项目中可以使用 Maven 来构建 jar 包。
