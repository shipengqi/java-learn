# 加密和安全

## 编码算法

`ASCII`，`UTF-8` 等都是编码。**编码算法，不是加密算法**。

最简单的编码是直接给每个字符指定一个若干字节表示的整数，复杂一点的编码就需要根据一个已有的编码推算出来。

### URL 编码

URL 编码是浏览器发送数据给服务器时使用的编码，通常附加在 URL 的参数部分，如：<https://www.baidu.com/s?wd=%E4%B8%AD%E6%96%87>

使用 URL 编码是为了解决兼容性的问题，一些服务器值能识别 ASCII 编码，如果 URL 包括中文等字符，就需要 URL 编码。

```java
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Main {
  public static void main(Stringp[] args) {
    String encoded = URLEncoder.encode("中文!", StandardCharsets.UTF_8);
    System.out.println(encoded); // %E4%B8%AD%E6%96%87%21
  }
}
```

`URLEncoder` 把空格字符编码成+，而现在的 URL 编码标准要求空格被编码为 `%20`，不过，服务器都可以处理这两种情况。

### Base64 编码

URL 编码是对字符进行编码，Base64 编码是对二进制数据进行编码，表示成文本格式。

Base64 编码可以把任意长度的二进制数据变为纯文本，且只包含 `A~Z`、`a~z`、`0~9`、`+`、`/`、`=` 这些字符。

```java
import java.util.*;

public class Main {
    public static void main(String[] args) {
        byte[] input = new byte[] { (byte) 0xe4, (byte) 0xb8, (byte) 0xad };
        String b64encoded = Base64.getEncoder().encodeToString(input);
        System.out.println(b64encoded);  // 5Lit
    }
}
```

```java
import java.util.*;

public class Main {
    public static void main(String[] args) {
        byte[] input = new byte[] { (byte) 0xe4, (byte) 0xb8, (byte) 0xad, 0x21 };
        String b64encoded = Base64.getEncoder().encodeToString(input);
        String b64encoded2 = Base64.getEncoder().withoutPadding().encodeToString(input);
        System.out.println(b64encoded);                            // 5LitIQ==
        System.out.println(b64encoded2);                           // 5LitIQ
        byte[] output = Base64.getDecoder().decode(b64encoded2);
        System.out.println(Arrays.toString(output));               // [-28, -72, -83, 33]
        output = Base64.getDecoder().decode(b64encoded);
        System.out.println(Arrays.toString(output)); // [-28, -72, -83, 33]
    }
}
```

Base64 编码会出现 `+`、`/` 和 `=`，所以不适合把 Base64 编码后的字符串放到 URL 中。

```java
import java.util.*;

public class Main {
    public static void main(String[] args) {
        byte[] input = new byte[] { 0x01, 0x02, 0x7f, 0x00 };
        String b64encoded = Base64.getUrlEncoder().encodeToString(input);
        System.out.println(b64encoded);
        byte[] output = Base64.getUrlDecoder().decode(b64encoded);
        System.out.println(Arrays.toString(output));
    }
}
```

## 哈希算法

### 哈希碰撞

哈希碰撞（哈希冲突），是指两个不同的输入得到了相同的输出。哈希算法的输出长度越长，就越难产生碰撞，也就越安全。

```java
import java.math.BigInteger;
import java.security.MessageDigest;

public class Main {
    public static void main(String[] args) throws Exception {
        // 创建一个MessageDigest实例:
        MessageDigest md = MessageDigest.getInstance("MD5");
        // 反复调用update输入数据:
        md.update("Hello".getBytes("UTF-8"));
        md.update("World".getBytes("UTF-8"));
        byte[] result = md.digest(); // 16 bytes: 68e109f0f40ca72a15e05cc22786f8e6
        System.out.println(new BigInteger(1, result).toString(16));
    }
}
```

### BouncyCastle

[BouncyCastle](https://www.bouncycastle.org/) 就是一个提供了很多哈希算法和加密算法的第三方库。

## Hmac

Hmac 算法就是一种基于密钥的消息认证码算法，全称是 Hash-based Message Authentication Code，是一种更安全的消息摘要算法。

Hmac 算法总是和某种哈希算法配合起来用的。如 HmacMD5 算法，它相当于“加盐”的 MD5：

- Hmac 使用的 key 长度是 64 字节，更安全；
- Hmac 是标准算法，同样适用于 `SHA-1` 等其他哈希算法；
- Hmac 输出和原有的哈希算法长度一致。

```java
import java.math.BigInteger;
import javax.crypto.*;

public class Main {
    public static void main(String[] args) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacMD5");
        SecretKey key = keyGen.generateKey();
        // 打印随机生成的key:
        byte[] skey = key.getEncoded();
        System.out.println(new BigInteger(1, skey).toString(16));
        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(key);
        mac.update("HelloWorld".getBytes("UTF-8"));
        byte[] result = mac.doFinal();
        System.out.println(new BigInteger(1, result).toString(16));
    }
}
```

## 对称加密

## 口令加密算法

## 密钥交换算法

## 非对称加密

## 签名算法

## 数字证书
