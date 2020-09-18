# 多线程

Java 内置了多线程的支持，一个 Java 程序实际上是一个 JVM 进程，JVM 进程用一个主线程来执行 `main` 方法，`main` 方法内部可以启动多个线程。

## 创建线程

```java
Thread t = new Thread();
t.start();
```

使新线程执行指定的代码。

1. 从 `Thread` 派生一个自定义类，覆写 `run` 方法：

```java
public class Main {
    public static void main(String[] args) {
        Thread t = new MyThread();
        t.start(); // 启动新线程
    }
}

class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("start new thread!");
    }
}

```

`start` 方法会在内部自动调用实例的 `run` 方法。

2. 创建 `Thread` 实例时，传入一个 `Runnable` 实例：

```java
public class Main {
    public static void main(String[] args) {
        Thread t = new Thread(new MyRunnable());
        t.start(); // 启动新线程
    }
}

class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("start new thread!");
    }
}


// Java8 引入的 lambda 语法
public class Main {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            System.out.println("start new thread!");
        });
        t.start(); // 启动新线程
    }
}

```

必须调用 `Thread` 实例的 `start()` 方法才能启动新线程，如果我们查看 `Thread` 类的源代码，会看到 `start()` 方法内部调用了一个 `private native void start0()` 方法，`native` 修饰符表示这个方法是由JVM虚拟机内部的C代码实现的，不是由 Java 代码实现的。

### 线程的优先级

```java
Thread.setPriority(int n) // 1~10, 默认值5
```

操作系统对高优先级线程可能调度更频繁，但不能通过设置优先级来确保高优先级的线程一定会先执行。

## 线程状态

Java 线程的状态：

- `New`：新创建的线程，尚未执行；
- `Runnable`：运行中的线程，正在执行 `run` 方法的 Java 代码；
- `Blocked`：运行中的线程，因为某些操作被阻塞而挂起；
- `Waiting`：运行中的线程，因为某些操作在等待中；
- `Timed Waiting`：运行中的线程，因为执行 `sleep` 方法正在计时等待；
- `Terminated`：线程已终止，因为 `run` 方法执行完毕

线程终止的原因有：

- 线程正常终止：`run` 方法执行到 `return` 语句返回；
- 线程意外终止：`run` 方法因为未捕获的异常导致线程终止；
- 对某个线程的 `Thread` 实例调用 `stop` 方法强制终止（强烈不推荐）。

`join` 一个线程还可以等待另一个线程直到其运行结束：

```java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            System.out.println("hello");
        });
        System.out.println("start");
        t.start();
        t.join();
        System.out.println("end");
    }
}
```

main 线程在启动 t 线程后，可以通过 `t.join` 等待 t 线程结束后再继续运行。`join(long)` 的重载方法也可以指定一个等待时间，超过等待时间后就不再继续等待。

## 中断线程

### volatile

线程间共享的变量用要关键字 `volatile` 声明。因为在 Java 虚拟机中，变量的值保存在主内存中，但是，当线程访问变量时，它会先获取一个副本，并保存在自己的工作内存中。如果线程修改了变量的值，虚拟机会在某个时刻把修改后的值回写到主内存，这个时间是不确定的！这会导致如果一个线程更新了某个变量，另一个线程读取的值可能还是更新前的。

`volatile` 关键字的目的是告诉虚拟机：

- 每次访问变量时，总是获取主内存的最新值；
- 每次修改变量后，立刻回写到主内存。

## 守护线程

在 JVM 中，所有非守护线程都执行完毕后，无论有没有守护线程，虚拟机都会自动退出。

```java
Thread t = new MyThread();
t.setDaemon(true);
t.start();
```

在调用 `start()` 方法前，调用 `setDaemon(true)` 把该线程标记为守护线程。

守护线程不能持有任何需要关闭的资源，例如打开文件等，因为虚拟机退出时，守护线程没有任何机会来关闭文件，这会导致数据丢失。

## 线程同步

如果多个线程同时读写共享变量，会出现数据不一致的问题。

假设 n 的值是 100，如果两个线程同时执行 `n = n + 1`，得到的结果很可能不是 102，而是 101，原因在于：

┌───────┐    ┌───────┐
│Thread1│    │Thread2│
└───┬───┘    └───┬───┘
    │            │
    │ILOAD (100) │
    │            │ILOAD (100)
    │            │IADD
    │            │ISTORE (101)
    │IADD        │
    │ISTORE (101)│
    ▼            ▼

多线程模型下，要保证逻辑正确，对共享变量进行读写时，必须保证一组指令以原子方式执行。原子操作是指不能被中断的一个或一系列操作。

┌───────┐     ┌───────┐
│Thread1│     │Thread2│
└───┬───┘     └───┬───┘
    │             │
    │-- lock --   │
    │ILOAD (100)  │
    │IADD         │
    │ISTORE (101) │
    │-- unlock -- │
    │             │-- lock --
    │             │ILOAD (101)
    │             │IADD
    │             │ISTORE (102)
    │             │-- unlock --
    ▼             ▼

通过加锁和解锁的操作，就能保证 3 条指令总是在一个线程执行期间，不会有其他线程会进入此指令区间。加锁和解锁之间的代码块我们称之为**临界区**（Critical Section），任何时候临界区最多只有一个线程能执行。

### synchronized

```java
synchronized(Counter.lock) { // 获取锁
    ...
} // 释放锁
```

`synchronized` 解决了多线程同步访问共享变量的正确性问题。但是，它的缺点是带来了性能下降。因为 `synchronized` 代码块无法并发执行。此外，加锁和解锁需要消耗一定的时间，所以， `synchronized` 会降低程序的执行效率。

### 不需要 `synchronized` 的操作

JVM 规范定义了几种原子操作：

- 基本类型（`long` 和 `double` 除外）赋值，例如：`int n = m`；
- 引用类型赋值，例如：`List<String> list = anotherList`。

## 同步方法

如果一个类被设计为允许多线程正确访问，我们就说这个类就是**线程安全**的（thread-safe）。

```java
public class Counter {
    private int count = 0;

    public void add(int n) {
        synchronized(this) {
            count += n;
        }
    }

    public void dec(int n) {
        synchronized(this) {
            count -= n;
        }
    }

    public int get() {
        return count;
    }
}
```

`Counter` 类就是线程安全的。`java.lang.StringBuffer` 也是线程安全的。

还有一些不变类，例如String，Integer，LocalDate，它们的所有成员变量都是final，多线程同时访问时只能读不能写，这些不变类也是线程安全的。

最后，类似Math这些只提供静态方法，没有成员变量的类，也是线程安全的。

用synchronized修饰的方法就是同步方法，它表示整个方法都必须用this实例加锁。

2.线程安全的解决方案

(1)以上例子,最常用的解决方案就是使用同步语句块 synchronized来保护取款过程.当一个线程在访问该账号正在执行取款操作时,其他线程想要进行取款只能等待.这种方式是以时间换空间.

(2)使用ThreadLocal,ThreadLocal维护变量时，ThreadLocal为每个使用该变量的线程提供独立的变量副本，所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。这种方式是以空间换时间.

好了,思维拓展结束.现在我们看问题,String为什么是线程安全的呢?

遍寻String的源码,你可能找不到几个synchronized关键字来.是的.我是在引导你走(2).

可是也没有看到ThreadLocal相关的东西啊.这是因为String在设计的时候字符的存储是放在char数组中的,而这个char数组是final的.也就是说,无论你是怎样的多线程环境.你做得修改操作

对原有的对象是没有任何影响的.因为String 在更改的时候是指向了另一个对象(也就是另一个char数组).每个线程修改的时候都是独立的一个char数组,这用的正是(2)方法.所以这个final的char数组才是String 安全的根本.

## 死锁

JVM允许同一个线程重复获取同一个锁，这种能被同一个线程反复获取的锁，就叫做可重入锁。由于Java的线程锁是可重入锁，所以，获取锁的时候，不但要判断是否是第一次获取，还要记录这是第几次获取。每获取一次锁，记录+1，每退出synchronized块，记录-1，减到0的时候，才会真正释放锁。

在获取多个锁的时候，不同线程获取多个不同对象的锁可能导致死锁。对于上述代码，线程1和线程2如果分别执行add()和dec()方法时：

线程1：进入add()，获得lockA；
线程2：进入dec()，获得lockB。
随后：

线程1：准备获得lockB，失败，等待中；
线程2：准备获得lockA，失败，等待中。

此时，两个线程各自持有不同的锁，然后各自试图获取对方手里的锁，造成了双方无限等待下去，这就是死锁。

在编写多线程应用时，要特别注意防止死锁。因为死锁一旦形成，就只能强制结束进程。

如何避免死锁呢？答案是：线程获取锁的顺序要一致。即严格按照先获取lockA，再获取lockB的顺序，改写dec()方法如下：

```java
public void dec(int m) {
    synchronized(lockA) { // 获得lockA的锁
        this.value -= m;
        synchronized(lockB) { // 获得lockB的锁
            this.another -= m;
        } // 释放lockB的锁
    } // 释放lockA的锁
}
```

## wait 和 notify

```java
public synchronized String getTask() {
    while (queue.isEmpty()) {
        this.wait();
    }
    return queue.remove();
}
```

线程执行while条件判断，如果条件成立（队列为空），线程将执行this.wait()，进入等待状态。

调用wait()方法后，线程进入等待状态，wait()方法不会返回，直到将来某个时刻，线程从等待状态被其他线程唤醒后，wait()方法才会返回，然后，继续执行下一条语句。

即使线程在getTask()内部等待，其他线程如果拿不到this锁，照样无法执行addTask()?

wait()方法的执行机制非常复杂。首先，它不是一个普通的Java方法，而是定义在Object类的一个native方法，也就是由JVM的C代码实现的。其次，必须在synchronized块中才能调用wait()方法，因为wait()方法调用时，会释放线程获得的锁，wait()方法返回后，线程又会重新试图获得锁。

因此，只能在锁对象上调用wait()方法。

如何让等待的线程被重新唤醒，然后从wait()方法返回？答案是在相同的锁对象上调用notify()方法。我们修改addTask()如下：

```java
public synchronized void addTask(String s) {
    this.queue.add(s);
    this.notify(); // 唤醒在this锁等待的线程
}
```

往队列中添加了任务后，线程立刻对this锁对象调用notify()方法，这个方法会唤醒一个正在this锁等待的线程（就是在getTask()中位于this.wait()的线程），从而使得等待线程从this.wait()方法返回。

使用notifyAll()将唤醒所有当前正在this锁等待的线程，而notify()只会唤醒其中一个（具体哪个依赖操作系统，有一定的随机性）。这是因为可能有多个线程正在getTask()方法内部的wait()中等待，使用notifyAll()将一次性全部唤醒。通常来说，notifyAll()更安全。有些时候，如果我们的代码逻辑考虑不周，用notify()会导致只唤醒了一个线程，而其他线程可能永远等待下去醒不过来了。

注意到wait()方法返回时需要重新获得this锁。假设当前有3个线程被唤醒，唤醒后，首先要等待执行addTask()的线程结束此方法后，才能释放this锁，随后，这3个线程中只能有一个获取到this锁，剩下两个将继续等待。

## ReentrantLock

`java.util.concurrent` 包提供了更高级的并发功能，可以简化多线程的编写。

使用 `synchronized` 关键字加锁，这种锁很重，获取时必须一直等待，没有别的尝试机制。

`java.util.concurrent.locks` 包的 `ReentrantLock` 可以替代 `synchronized` 加锁。

因为synchronized是Java语言层面提供的语法，所以我们不需要考虑异常，而ReentrantLock是Java代码实现的锁，我们就必须先获取锁，然后在finally中正确释放锁。

ReentrantLock是可重入锁，它和synchronized一样，一个线程可以多次获取同一个锁。

## Condition

synchronized可以配合wait和notify实现线程在条件不满足时等待，条件满足时唤醒，用ReentrantLock我们怎么编写wait和notify的功能呢？

答案是使用Condition对象来实现wait和notify的功能。

```java
class TaskQueue {
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private Queue<String> queue = new LinkedList<>();

    public void addTask(String s) {
        lock.lock();
        try {
            queue.add(s);
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public String getTask() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                condition.await();
            }
            return queue.remove();
        } finally {
            lock.unlock();
        }
    }
}
```

Condition提供的await()、signal()、signalAll()原理和synchronized锁对象的wait()、notify()、notifyAll()是一致的

await()可以在等待指定时间后，如果还没有被其他线程通过signal()或signalAll()唤醒，可以自己醒来：

```java
if (condition.await(1, TimeUnit.SECOND)) {
    // 被其他线程唤醒
} else {
    // 指定时间内没有被其他线程唤醒
}
```

## ReadWriteLock

允许多个线程同时读，但只要有一个线程在写，其他线程就必须等待，使用 `ReadWriteLock`

只允许一个线程写入（其他线程既不能写入也不能读取）；
没有写入时，多个线程允许同时读（提高性能）。

```java
public class Counter {
    private final ReadWriteLock rwlock = new ReentrantReadWriteLock();
    private final Lock rlock = rwlock.readLock();
    private final Lock wlock = rwlock.writeLock();
    private int[] counts = new int[10];

    public void inc(int index) {
        wlock.lock(); // 加写锁
        try {
            counts[index] += 1;
        } finally {
            wlock.unlock(); // 释放写锁
        }
    }

    public int[] get() {
        rlock.lock(); // 加读锁
        try {
            return Arrays.copyOf(counts, counts.length);
        } finally {
            rlock.unlock(); // 释放读锁
        }
    }
}
```

读写操作分别用读锁和写锁来加锁，在读取时，多个线程可以同时获得读锁，这样就大大提高了并发读的执行效率。

## StampedLock

深入分析ReadWriteLock，会发现它有个潜在的问题：如果有线程正在读，写线程需要等待读线程释放锁后才能获取写锁，即读的过程中不允许写，这是一种悲观的读锁。

要进一步提升并发执行效率，Java 8引入了新的读写锁：StampedLock。

StampedLock和ReadWriteLock相比，改进之处在于：读的过程中也允许获取写锁后写入！这样一来，我们读的数据就可能不一致，所以，需要一点额外的代码来判断读的过程中是否有写入，这种读锁是一种乐观锁。

乐观锁的意思就是乐观地估计读的过程中大概率不会有写入，因此被称为乐观锁。反过来，悲观锁则是读的过程中拒绝有写入，也就是写入必须等待。显然乐观锁的并发效率更高，但一旦有小概率的写入导致读取的数据不一致，需要能检测出来，再读一遍就行。

```java
public class Point {
    private final StampedLock stampedLock = new StampedLock();

    private double x;
    private double y;

    public void move(double deltaX, double deltaY) {
        long stamp = stampedLock.writeLock(); // 获取写锁
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            stampedLock.unlockWrite(stamp); // 释放写锁
        }
    }

    public double distanceFromOrigin() {
        long stamp = stampedLock.tryOptimisticRead(); // 获得一个乐观读锁
        // 注意下面两行代码不是原子操作
        // 假设x,y = (100,200)
        double currentX = x;
        // 此处已读取到x=100，但x,y可能被写线程修改为(300,400)
        double currentY = y;
        // 此处已读取到y，如果没有写入，读取是正确的(100,200)
        // 如果有写入，读取是错误的(100,400)
        if (!stampedLock.validate(stamp)) { // 检查乐观读锁后是否有其他写锁发生
            stamp = stampedLock.readLock(); // 获取一个悲观读锁
            try {
                currentX = x;
                currentY = y;
            } finally {
                stampedLock.unlockRead(stamp); // 释放悲观读锁
            }
        }
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }
}
```

和ReadWriteLock相比，写入的加锁是完全一样的，不同的是读取。注意到首先我们通过tryOptimisticRead()获取一个乐观读锁，并返回版本号。接着进行读取，读取完成后，我们通过validate()去验证版本号，如果在读取过程中没有写入，版本号不变，验证成功，我们就可以放心地继续后续操作。如果在读取过程中有写入，版本号会发生变化，验证将失败。在失败的时候，我们再通过获取悲观读锁再次读取。由于写入的概率不高，程序在绝大部分情况下可以通过乐观读锁获取数据，极少数情况下使用悲观读锁获取数据。

## 线程安全的集合

针对List、Map、Set、Deque等，`java.util.concurrent` 包也提供了对应的并发集合类。

interface non-thread-safe thread-safe
List ArrayList CopyOnWriteArrayList
Map HashMap ConcurrentHashMap
Set HashSet / TreeSet CopyOnWriteArraySet
Queue ArrayDeque / LinkedList ArrayBlockingQueue / LinkedBlockingQueue
Deque ArrayDeque / LinkedList LinkedBlockingDeque

这些并发集合与使用非线程安全的集合类完全相同。

java.util.Collections工具类还提供了一个旧的线程安全集合转换器，可以这么用：

Map unsafeMap = new HashMap();
Map threadSafeMap = Collections.synchronizedMap(unsafeMap);
但是它实际上是用一个包装类包装了非线程安全的Map，然后对所有读写方法都用synchronized加锁，这样获得的线程安全集合的性能比java.util.concurrent集合要低很多，所以不推荐使用。

## Atomic

`java.util.concurrent.atomic` 包，提供了一组原子操作的封装类

Atomic类是通过无锁（lock-free）的方式实现的线程安全（thread-safe）访问。它的主要原理是利用了CAS：Compare and Set。

以AtomicInteger为例，它提供的主要操作有：

增加值并返回新值：int addAndGet(int delta)
加1后返回新值：int incrementAndGet()
获取当前值：int get()
用CAS方式设置：int compareAndSet(int expect, int update)

## 线程池

Java语言虽然内置了多线程支持，启动一个新线程非常方便，但是，创建线程需要操作系统资源（线程资源，栈空间等），频繁创建和销毁大量线程需要消耗大量时间

如果可以复用一组线程：

┌─────┐ execute  ┌──────────────────┐
│Task1│─────────>│ThreadPool        │
├─────┤          │┌───────┐┌───────┐│
│Task2│          ││Thread1││Thread2││
├─────┤          │└───────┘└───────┘│
│Task3│          │┌───────┐┌───────┐│
├─────┤          ││Thread3││Thread4││
│Task4│          │└───────┘└───────┘│
├─────┤          └──────────────────┘
│Task5│
├─────┤
│Task6│
└─────┘
  ...

那么我们就可以把很多小任务让一组线程来执行，而不是一个任务对应一个新线程。这种能接收大量小任务并进行分发处理的就是线程池。

ExecutorService接口表示线程池，它的典型用法如下：

```java
// 创建固定大小的线程池:
ExecutorService executor = Executors.newFixedThreadPool(3);
// 提交任务:
executor.submit(task1);
executor.submit(task2);
executor.submit(task3);
executor.submit(task4);
executor.submit(task5);
```

ExecutorService只是接口，Java标准库提供的几个常用实现类有：

FixedThreadPool：线程数固定的线程池；
CachedThreadPool：线程数根据任务动态调整的线程池；
SingleThreadExecutor：仅单线程执行的线程池。

```java
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        // 创建一个固定大小的线程池:
        ExecutorService es = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 6; i++) {
            es.submit(new Task("" + i));
        }
        // 关闭线程池:
        es.shutdown();
    }
}

class Task implements Runnable {
    private final String name;

    public Task(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println("start task " + name);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        System.out.println("end task " + name);
    }
}

```

```bash
start task 1
start task 2
start task 3
start task 0
end task 1
end task 3
end task 0
end task 2
start task 4
start task 5
end task 4
end task 5
```

一次性放入6个任务，由于线程池只有固定的4个线程，因此，前4个任务会同时执行，等到有线程空闲后，才会执行后面的两个任务。

使用shutdown()方法关闭线程池的时候，它会等待正在执行的任务先完成，然后再关闭。shutdownNow()会立刻停止正在执行的任务，awaitTermination()则会等待指定的时间让线程池关闭。

把线程池改为CachedThreadPool，由于这个线程池的实现会根据任务数量动态调整线程池的大小，所以6个任务可一次性全部同时执行。

如果我们想把线程池的大小限制在4～10个之间动态调整怎么办？我们查看Executors.newCachedThreadPool()方法的源码：

```java
public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                    60L, TimeUnit.SECONDS,
                                    new SynchronousQueue<Runnable>());
}
```

因此，想创建指定动态范围的线程池，可以这么写：

```java
int min = 4;
int max = 10;
ExecutorService es = new ThreadPoolExecutor(min, max,
        60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
```

### ScheduledThreadPool

创建一个ScheduledThreadPool仍然是通过Executors类：

ScheduledExecutorService ses = Executors.newScheduledThreadPool(4);
我们可以提交一次性任务，它会在指定延迟后只执行一次：

// 1秒后执行一次性任务:
ses.schedule(new Task("one-time"), 1, TimeUnit.SECONDS);
如果任务以固定的每3秒执行，我们可以这样写：

// 2秒后开始执行定时任务，每3秒执行:
ses.scheduleAtFixedRate(new Task("fixed-rate"), 2, 3, TimeUnit.SECONDS);
如果任务以固定的3秒为间隔执行，我们可以这样写：

// 2秒后开始执行定时任务，以3秒为间隔执行:
ses.scheduleWithFixedDelay(new Task("fixed-delay"), 2, 3, TimeUnit.SECONDS);
注意FixedRate和FixedDelay的区别。FixedRate是指任务总是以固定时间间隔触发，不管任务执行多长时间：

│░░░░   │░░░░░░ │░░░    │░░░░░  │░░░  
├───────┼───────┼───────┼───────┼────>
│<─────>│<─────>│<─────>│<─────>│
而FixedDelay是指，上一次任务执行完毕后，等待固定的时间间隔，再执行下一次任务：

│░░░│       │░░░░░│       │░░│       │░
└───┼───────┼─────┼───────┼──┼───────┼──>
    │<─────>│     │<─────>│  │<─────>│

## Future

Java标准库提供了一个Callable接口，和Runnable接口比，它多了一个返回值。并且Callable接口是一个泛型接口，可以返回指定类型的结果。

如何获得异步执行的结果？

```java
ExecutorService executor = Executors.newFixedThreadPool(4);
// 定义任务:
Callable<String> task = new Task();
// 提交任务并获得Future:
Future<String> future = executor.submit(task);
// 从Future获取异步执行返回的结果:
String result = future.get(); // 可能阻塞
```

executorService.submit()方法，可以看到，它返回了一个Future类型，一个Future类型的实例代表一个未来能获取结果的对象。

在调用get()时，如果异步任务已经完成，我们就直接获得结果。如果异步任务还没有完成，那么get()会阻塞，直到任务完成后才返回结果。

一个Future<V>接口表示一个未来可能会返回的结果，它定义的方法有：

get()：获取结果（可能会等待）
get(long timeout, TimeUnit unit)：获取结果，但只等待指定的时间；
cancel(boolean mayInterruptIfRunning)：取消当前任务；
isDone()：判断任务是否已完成。

## CompletableFuture

Java 8开始引入了CompletableFuture，它针对Future做了改进，可以传入回调对象，当异步任务完成或者发生异常时，自动调用回调对象的回调方法。

```java
import java.util.concurrent.CompletableFuture;

public class Main {
    public static void main(String[] args) throws Exception {
        // 创建异步执行任务:
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(Main::fetchPrice);
        // 如果执行成功:
        cf.thenAccept((result) -> {
            System.out.println("price: " + result);
        });
        // 如果执行异常:
        cf.exceptionally((e) -> {
            e.printStackTrace();
            return null;
        });
        // 主线程不要立刻结束，否则CompletableFuture默认使用的线程池会立刻关闭:
        Thread.sleep(200);
    }

    static Double fetchPrice() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        if (Math.random() < 0.3) {
            throw new RuntimeException("fetch price failed!");
        }
        return 5 + Math.random() * 20;
    }
}

```

通过 `CompletableFuture.supplyAsync()` 创建一个 `CompletableFuture`：

它需要一个实现了Supplier接口的对象：

```java
public interface Supplier<T> {
    T get();
}
```

lambda语法简化了一下，直接传入Main::fetchPrice，因为Main.fetchPrice()静态方法的签名符合Supplier接口的定义

`CompletableFuture` 被提交给默认的线程池执行

需要定义的是CompletableFuture完成时和异常时需要回调的实例。完成时，CompletableFuture会调用Consumer对象：

```java
public interface Consumer<T> {
    void accept(T t);
}
```

异常时，CompletableFuture会调用Function对象：

```java
public interface Function<T, R> {
    R apply(T t);
}
```

这里我们都用lambda语法简化了代码。

多个CompletableFuture可以串行执行
多个CompletableFuture还可以并行执行

## ForkJoin

Java 7开始引入了一种新的Fork/Join线程池，**它可以执行一种特殊的任务，把多个任务拆成多个小任务并行执行**

Fork/Join 任务的原理，判断一个任务是否足够小，如果是，直接计算，否则，就拆成几个小任务分别计算，这个过程可以反复裂变成一系列小任务。

## ThreadLocal

`Thread.currentThread()` 可以获取当前线程。

如何在一个线程内传递状态？

这种在一个线程中，横跨若干方法调用，需要传递的对象，我们通常称之为上下文（Context），它是一种状态，可以是用户身份、任务信息等。

Java标准库提供了一个特殊的ThreadLocal，它可以在一个线程中传递同一个对象。

ThreadLocal 实例通常以静态字段初始化：

```java
static ThreadLocal<User> threadLocalUser = new ThreadLocal<>();
```

```java
void process(user) {
    try {
        threadLocalUser.set(user);
        // do something ...
    } finally {
        threadLocalUser.remove();
    }
}
```

**ThreadLocal 可以看做是一个全局的 `Map<Thread, Object>`，每个线程获取 ThreadLocal 变量时，总是使用 `Thread` 自身作为 key**。

ThreadLocal 一定要在 `finally` 中清除。这是因为当前线程执行完以后，可能会被放入线程池中，如果没有清除，再次使用该线程时，会把状态带进去。

为了保证能释放ThreadLocal关联的实例，我们可以通过AutoCloseable接口配合`try (resource) {...}`结构，让编译器自动为我们关闭。例如，一个保存了当前用户名的ThreadLocal可以封装为一个UserContext对象：

```java
public class UserContext implements AutoCloseable {

    static final ThreadLocal<String> ctx = new ThreadLocal<>();

    public UserContext(String user) {
        ctx.set(user);
    }

    public static String currentUser() {
        return ctx.get();
    }

    @Override
    public void close() {
        ctx.remove();
    }
}


try (var ctx = new UserContext("Bob")) {
    // 可任意调用UserContext.currentUser():
    String currentUser = UserContext.currentUser();
} // 在此自动调用UserContext.close()方法释放ThreadLocal关联对象
```
