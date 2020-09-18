# IO

Input 指从外部读入数据到内存，例如，把文件从磁盘读取到内存，从网络读取数据到内存等等。
Output 指把数据从内存输出到外部，例如，把数据从内存写入到文件，把数据从内存输出到网络等等。

## InputStream/OutputStream

IO 流以 byte 为单位，也叫做**字节流**。

### InputStream

`InputStream` 是一个抽象类，最基本的输入流。最重要的抽象方法 `public abstract int read() throws IOException;`。

- `FileInputStream` 从文件读取数据
- `ServletInputStream` 从 HTTP 请求读取数据
- `Socket.getInputStream()` 从 TCP 连接读取数据
- `ByteArrayInputStream`

### OutputStream

`OutputStream` 和 `InputStream` 类似，是基本的输出流。最重要的抽象方法 `public abstract void write(int b) throws IOException;`。虽然传入的是 `int` ，但是只会写入一个字节，只写入 `int` 最低 8 位表示的字节。

## Reader/Writer

如果需要读写字符，并且字符不全是 ASCII 字符，那么以 `char` 来读写更方便，这种流叫**字符流**。

`Reader/Writer` 用来表示字符流，最小传输单位为 `char`。

`Reader` 会把读入的 `byte` 做解码，转成 `char`。`InputStream` 读入的也是 `byte`，但是可以自己把 `byte[]` 按照某种编码转成字符串。如果数据源不是文本就只能使用 `InputStream`。

### PrintStream 和 PrintWriter

## 同步和异步

`java.io` 提供了同步 IO，`java.nio` 提供了异步 IO。

## File

```java
import java.io.*;

public class Main {
  public static void main(Stirngp[] args) {
    File f = new File("..");
    System.out.println(f);
    System.out,println(f.getPath());   // ..   构造方法传入的路径
    System.out,println(f.getAbsolutePath());   // /app/.. 绝对路径
    System.out,println(f.getCanonicalPath());   // / 规范路径，就是把 . 和 .. 转成标准的绝对路径后的路径
  }
}
```

**构造一个 `File` 对象，传入的文件或者目录不存在，不会出错，因为构造 `File` 对象是不会进行磁盘操作的**。只有调用 `File` 对象的方法是才会操作磁盘。

### Path

Path 对象在 `java.nio.file` 包。和 File 对象类似，但是如果需要对目录进行复杂拼接或遍历等操作，使用 Path 更方便。

```java
import java.io.*;
import java.nio.file.*;

public class Main {
  public static void main(Stirngp[] args) {
    Path p1 = new Path("..");
    Path p2 = p1.toAbsolutePath(); // 转换为绝对路径
    Path p3 = p2.normalize(); // 转换为规范路径
    File f = p3.toFile(); // 转换为 File 对象
    for (Path p : Paths.get("..").toAbsolutePath()) { // 可以直接遍历 Path
      System.out.println("  " + p);
    }
  }
}
```

## FilterInputStream

### ZipInputStream

## 序列化

序列化是指把一个 Java 对象变成二进制，本质上就是一个 `byte[]`。反序列化正相反。

序列化后的 Java 对象就可以把  `byte[]` 保存到文件，或者网络传输出去。反序列化可以把文件中或者网络传输的 `byte[]` 变为 Java 对象。

Java 对象要实现序列化，必须实现 `java.io.Serializable` 接口：

```java
public interface Seriablizable {

}
```

`Seriablizable` 是一个空接口，这种空接口叫做**标记接口**（Marker Interface）。

Java 对象序列化需要使用 `ObjectOutputStream`。可以写入基本类型和实现了 `Serializable` 接口的的 `Object`。

```java
import java.io.*;
import java.util.Arrays;

public class Main {
  public static void main(Stringp[] args) throw IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    try (ObjectOutputStream output = new ObjectOutputStream(buffer)) {
      output.writeInt(12345);
      output.writeUTF("Hello");
      output.writeObject(Double.valueOf(123.456));
    }
    System.out.println(Arrays.toString(buffer.toByteArray()));
  }
}
```

反序列化需要使用 `ObjectInputStream`，负责从一个字节流读取 Java 对象。

```java
try (ObjectInputStream input = new ObjectInputStream(...)) {
    int n = input.readInt();
    String s = input.readUTF();
    Double d = (Double) input.readObject();
}
```

`readObject` 可能抛出异常：

- `ClassNotFoundException` 没有找到对应的 class
- `InvalidClassException` class 不匹配
