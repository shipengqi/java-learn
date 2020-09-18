# 正则表达式

`java.util.regex` 包内置了正则表达式引擎。

判断是否是 `20**` 年：

```java
punlic class Main {
  public static void main(String[] args) {
    String reg = "20\\d\\d";
    System.out.println("2019".matces(reg)); // true
  }_
}
```
