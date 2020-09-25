# 网络编程

Socket、TCP 和部分 IP 的功能都是由操作系统提供的，不同的的语言只是提供了对操作系统调用的简单封装。

一个 Socket 是由 IP 和端都号组成，端口号是 `0~65535` 之间的数字，由操作系统分配。小于 1024 的端口属于**特权端口**。

socket 连接建立后：

- 对于服务端，socket 是指定的 IP 和端口号
- 对于客户端，socket 是客户端所在的机器的 IP 和操作系统随机分配的端口号

## TCP

服务端程序可以使用 `ServerSocket` 来实现：

```java
public class server {
  public static void main(String[] args) throw IOException {
    ServerSocket ss = new ServerSocker(8080); // 监听端口
    System.out.println("server is running ...");
    for (;;) {
      Socket sock = ss.accept();
      System.out.println("connected from " + sock.getRemoteSocketAddress());
      Thread t = new Handler(sock);
      t.start();
    }
  }
}

class Handler extends Thread {
  Socket sock;

  public Handler(Socket sock) {
    this.sock = sock;
  }

  @Override
  public void run() {

  }
}
```

## UDP

UDP 不需要创建连接，数据包一次收发一个，没有流的概念。UDP 的端口和 TCP 的端口范围都是 `0~65535`，但他们是两套独立的端口，比如，一个 TCP 程序占用了 8080，另一个 UDP 仍然可以使用 8080 端口。

UDP 使用 `DatagramSocket` 实现：

```java
DatagtamSocket ds = new DatagramSocket(8080);
for (;;) {

}
```

## Email

我们把类似Outlook这样的邮件软件称为MUA：Mail User Agent，意思是给用户服务的邮件代理；邮件服务器则称为MTA：Mail Transfer Agent，意思是邮件中转的代理；最终到达的邮件服务器称为MDA：Mail Delivery Agent，意思是邮件到达的代理。电子邮件一旦到达MDA，就不再动了。实际上，电子邮件通常就存储在MDA服务器的硬盘上，然后等收件人通过软件或者登陆浏览器查看邮件。

MUA到MTA发送邮件的协议就是 SMTP 协议，它是 Simple Mail Transport Protocol的缩写，使用标准端口 25，也可以使用加密端口 465 或 587。

一些常用邮件服务商的 SMTP 信息：

- QQ 邮箱：SMTP 服务器是 `smtp.qq.com`，端口是 `465/587`；
- 163 邮箱：SMTP 服务器是 `smtp.163.com`，端口是 `465`；
- Gmail 邮箱：SMTP 服务器是 `smtp.gmail.com`，端口是 `465/587`。

`JavaMail` 相关的两个依赖：

```xml
    <dependency>
        <groupId>javax.mail</groupId>
        <artifactId>javax.mail-api</artifactId>
        <version>1.6.2</version>
    </dependency>
    <dependency>
        <groupId>com.sun.mail</groupId>
        <artifactId>javax.mail</artifactId>
        <version>1.6.2</version>
    </dependency>
```

接收邮件使用最广泛的协议是POP3：Post Office Protocol version 3，它也是一个建立在TCP连接之上的协议。POP3服务器的标准端口是110，如果整个会话需要加密，那么使用加密端口995。

另一种接收邮件的协议是IMAP：Internet Mail Access Protocol，它使用标准端口143和加密端口993。IMAP和POP3的主要区别是，IMAP协议在本地的所有操作都会自动同步到服务器上，并且，IMAP可以允许用户在邮件服务器的收件箱中创建文件夹。

## HTTP

## RMI

RMI远程调用是指，一个JVM中的代码可以通过网络实现远程调用另一个JVM的某个方法。RMI是Remote Method Invocation的缩写。
