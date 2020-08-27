package oop.core.string;

public class Main {
    public static void main(String[] args) {
        String s = "Hello";
        System.out.println(s);
        s = s.toUpperCase();
        System.out.println(s);

        String s1 = "hello";
        String s2 = "hello";
        System.out.println(s1 == s2);
        System.out.println(s1.equals(s2));

        String s3 = "hello";
        String s4 = "HELLO".toLowerCase();
        System.out.println(s3 == s4);
        System.out.println(s3.equals(s4));

        String s5 = "Hello";
        System.out.println(s5.equalsIgnoreCase("hello"));
        System.out.println(s5.contains("ll"));
        System.out.println(s5.indexOf("l"));
        System.out.println(s5.lastIndexOf("l"));
        System.out.println(s5.startsWith("He"));
        System.out.println(s5.endsWith("lo"));
        System.out.println(s5.substring(2));
        System.out.println(s5.substring(2, 4));

        System.out.println("   \tHello\r\n ".trim());

    }
}
