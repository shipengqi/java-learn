package oop.core.striingjoiner;

import java.util.StringJoiner;

public class Main {
    public static void main(String[] args) {
        String[] names = {"Pooky", "Alice", "Bob"};
        var sj = new StringJoiner(", ");
        for (String name : names) {
            sj.add(name);
        }
        System.out.println(sj.toString());

        var sj2 = new StringJoiner(", ", "Hello ", "!");
        for (String name : names) {
            sj2.add(name);
        }
        System.out.println(sj2.toString());

        String s3 = String.join(", ", names);
        System.out.println(s3);
    }
}
