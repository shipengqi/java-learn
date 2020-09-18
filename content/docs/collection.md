# 集合

`java.util` 包提供了集合类：`Collection`，它是除 `Map` 外所有其他集合类的根接口。`java.util` 主要提供了以下三种类型的集合：

- `List`，有序列表集合
- `Set`：没有重复元素的集合
- `Map`：key-value 集合

集合的特点：

- 接口和实现类相分离，如，有序列表的接口是 `List`，具体的实现类有 `ArrayList`，`LinkedList` 等。
- 支持泛型
- 集合统一通过迭代器（Iterator）的方式实现访问。最明显的好处在于不需要知道集合内部元素是按什么方式存储的。

## List

数组和 List 几乎完全一样，内部元素按照顺序存放，下标从 0 开始。数组的插入和删除操作是非常麻烦的，比如删除，需要把删除下标位置后的所有元素向前或者向后挪一个位置。

在实际应用中，如果需要插入和删除元素，使用的一般是 `ArrayList`。`ArrayList` 底层依然是使用数组来存储元素。只不过封装了添加和删除操作。

`List<E>` 接口：
`boolean add(E e)`
`boolean add(int index, E e)`
`int remove(int index)`
`int remove(Object e)`
`E get(int index)`
`int size()`

`LinkedList` 也是先了 `List` 接口，通过链表实现的。

### 遍历

### equals 方法

## Map

`Map` 和 `List` 类似，也是接口，最常用的实现类是 `HashMap`

### 遍历

遍历 `Map` 的 `key` 可以使用 `for each` 循环遍历 `Map` 实例的 `keySet()` 返回的 `Set` 集合，包含不重复的 `key` 集合。

```java
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        map.put("apple", 123);
        map.put("pear", 456);
        map.put("banana", 789);
        for (String key : map.keySet()) {
            Integer value = map.get(key);
            System.out.println(key + " = " + value);
        }
    }
}
```

遍历 `Map` 的 key 和 value 可以使用 `for each` 遍历 `Map` 实例的 `entrySet()` 集合，包含每一个 key-value：

```java
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        map.put("apple", 123);
        map.put("pear", 456);
        map.put("banana", 789);
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println(key + " = " + value);
        }
    }
}
```

`Map` 是不保证顺序的。

## EnumMap

如果 key 的对象是 `enum` 类型，可以使用 `EnumMap`，它的底层使用非常紧凑的数组存储，并且根据 `enum` 的类型的 key 直接定位到数组的索引，并不需要计算 `hashCode()`，
效率高，不会浪费空间。

```java
import java.time.DayOfWeek;
import java.util.*;

public class Main {
    public static void main() {
        Map<DayOfWeek, String> map = new EnumMap<>(DayOfWeek.class);
        map.put(DayOfWeek.MONDAY, "星期一");
        map.put(DayOfWeek.THURSDAY, "星期二");
        map.put(DayOfWeek.WEDNESDAY, "星期三");
        map.put(DayOfWeek.THURSDAY, "星期四");
        map.put(DayOfWeek.FRIDAY, "星期五");
        map.put(DayOfWeek.SATURDAY, "星期六");
        map.put(DayOfWeek.SUNDAY, "星期日");
        System.out.println(map);
        System.out.println(map.get(DayOfWeek.MONDAY));
    }
}
```

## TreeMap

`HashMap` 是的 key 是无序的。但是 `SortedMap` 会在内部对 key 进行排序。`SortedMap` 是一个接口，它的实现类是 `TreeMap`。

SortedMap 可以保证遍历时按照 key 的顺序来进行排序，如 `String` 没默认按照字母排序。key 必须实现 `Comparable` 接口。

## Properties

Java 可以使用 `Properties` 来表示一组配置。Java 默认配置文件以 `.properties` 为扩展名，每行以 `key=value` 表示，以 `#` 开头是注释。
用Properties读取配置文件，一共有三步：

1. 创建 `Properties` 实例
2. 调用 `load()` 读取文件
3 调用 `getProperty()` 获取配置

```java
import java.io.FileInputStream;
import java.util.Properties;

public class Main {
    public static void main() {
        String f = "stttings.properties";
        Properties props = new Properties();
        props.load(new java.io.FileInputStream(f));

        String filepath = props.getProperty("filepath");
        props.setProperty("language", "Java");
        System.out.println(filepath);
    }
}
```

早期版本的 Java 规定 `.properties` 文件编码是 ASCII 编码。JDK 9 开始支持 `UTF-8` 编码。

`load(InputStream)` 默认是以 ASCII 编码读取字节流，所以会导致乱码。需要使用 `load(Reader)` 读取：

```java
Properties props = new Properties();
props.load(new FileReader("settings.properties", StandardCharsets.UTF_8));
```

就可以正常读取中文。

## Set

Set 用于存储不重复的元素集合：

- `boolean add(E e)`
- `boolean remove(Object e)`
- `boolean contains(Object e)`

```java
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        System.out.println(set.add("abc"));
        System.out.println(set.contains("abc")); // true
        System.out.println(set.remove("hello")); // false 元素不存在
        System.out.println(set.size()); // 1
    }
}
```

`Set` 实际上相当于只存储 key、不存储 value 的 `Map`。因为放入 Set 的 key 需要实现 `equals()` 和 `hashCode()` 方法，否则无法正确的放入 `Set`。

`Set` 接口最常用的实现类是 `HashSet`。实际上 `HashSet` 只是 `HashMap` 的一个简单封装。

`Set` 不保证顺序，但是 `SortedSet` 接口可以保证元素是有序的。`TreeSet` 类实现了 `SortedSet` 接口。

## Queue

`Queue` 队列是一个实现了先进先出的有序表。

- `int size()`
- `boolean add(E)/boolean offer(E)` 在队尾添加元素
- `E remove()/E poll()` 获取队首元素，并从队列中删除
- `E element()/E peek()` 获取队首元素，但不从队列中删除

添加、删除和获取队列元素总是有两个方法，这是因为在添加或获取元素失败时，这两个方法的行为是不同的。`add`、`remove`、`element` 方法失败会抛出异常。
`offer`、`poll`、`peek` 则是会返回 `false` 或者 `null`。

不要把 `null` 添加到队列中，否则 `poll()` 等方法返回 `null` 时，很难确定是取到了 `null` 元素还是队列为空。

```java
import java.util.LinkedList;
import java.util.Queue;

public class Main {
    public static void main(String[] args) {
        Queue<String> q = new LinkedList<>();
        q.offer("a");
        q.offer("b");
        System.out.println(q.poll()); // a
    }
}
```

LinkedList 实现了 List 接口，也实现了 Queue 接口：

```java
// 这是一个List:
List<String> list = new LinkedList<>();
// 这是一个Queue:
Queue<String> queue = new LinkedList<>();
```

## PriorityQueue

`PriorityQueue` 和 `Queue` 的区别在于，`PriorityQueue` 的出队顺序与元素的优先级有关，调用 `remove` 或 `poll` 方法时，返回的是优先级最高的
元素。

```java
import java.util.Queue;
import java.util.PriorityQueue;

public class Main {
    public static void main(String[] args) {
        Queue<String> q = new PriorityQueue<>();
        q.offer("apple");
        q.offer("pear");
        q.offer("banana");
        System.out.println(q.poll()); // apple
        System.out.println(q.poll()); // banana
        System.out.println(q.poll()); // pear
    }
}
```

## Deque

双端队列（Double Ended Queue），学名 `Deque`。

## Stack

Java 的集合类没有单独的 `Stack` 接口。因为有一个遗留类的名字叫 `Stack`。考虑到兼容性，不能再创建 `Stack` 接口。所以只能用 `Deque` 接口来模拟
`Satck`。

## Iterator

Java 的集合类都可以使用 `for each` 循环：

```java
List<String> l = List.of("a", "b", "c");
for (String v : l) {
    System.out.println(v);
}
```

Java 编译器并不知道如何遍历 `List`，编译器会把 `for each` 循环通过 `Iterater` 改写成了普通的 `for` 循环：

```java
for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
    String v = it.next();
    System.out.println(v);
}
```

通过 `Iterator` 对象遍历集合的模式称为迭代器。迭代器的好处是调用方以同意的方式遍历各种集合类型，不必关注它们内部的存储结构。

例如，`Arraylist` 内部是使用数组存储元素，并且提供了 `get(int)` 方法：

```java
for (int i-0; i < list.size();i++) {
   Object v = list.get(i);
}
```

这种方式就必须知道集合的内部存储结构。而且如果 `ArrayList` 换成 `LinkedList`，`get(int)` 获取元素的时间复杂度是 `O(n)`。如果换成 `Set` 就无法编译
，因为 `Set` 没有索引。

`Iterator` 对象是集合对象内部创建的，`Iterator` 对象 知道如何高效遍历内部的数据集合。

要使用 `for each` 循环，需要满足两点：

- 集合类实现了 `Iterator` 接口
- 用 `Iterator` 对象迭代集合内布数据

## Collections

`Collections` 在 `java.util` 包中。

### 创建空集合

- `List<T> emptyList()`
- `Map<K, V> emptyMap()`
- `Set<T> emptySet()`

**返回的空集合是不可变集合，无法向其中添加或删除元素**。也可以用各个集合接口提供的 `of(T...)` 方法创建空集合。

### 创建单元素集合

- `List<T> singletonList(T o)`
- `Map<K, V> singletonMap(K key, V value)`
- `Set<T> singleton(T o)`

**返回的是不可变集合，无法向其中添加或删除元素**。也可以用各个集合接口提供的 `of(T...)` 方法创建单元素集合。

### 排序

### 洗牌

洗牌算法，就是随机打乱元素的顺序。

### 不可变集合

### 线程安全集合
