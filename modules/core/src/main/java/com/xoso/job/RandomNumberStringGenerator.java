package com.xoso.job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomNumberStringGenerator {
    private static final String digits = "0123456789";
    private static final String digitsChan = "02468";
    private static final String digitsLe = "13579";

    public static String generate(int length) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(digits.length());
            builder.append(digits.charAt(index));
        }
        return builder.toString();
    }
    public static String generateChan(int length) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length-1; i++) {
            int index = random.nextInt(digits.length());
            builder.append(digits.charAt(index));
        }
        int index = random.nextInt(digitsChan.length());
        builder.append(digitsChan.charAt(index));
        return builder.toString();
    }
    public static String generateLe(int length) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length-1; i++) {
            int index = random.nextInt(digits.length());
            builder.append(digits.charAt(index));
        }
        int index = random.nextInt(digitsLe.length());
        builder.append(digitsLe.charAt(index));
        return builder.toString();
    }

    public static String generate7th(){
        String result=RandomNumberStringGenerator.generate(2)
                +"-"+RandomNumberStringGenerator.generate(2)
                +"-"+RandomNumberStringGenerator.generate(2)
                +"-"+RandomNumberStringGenerator.generate(2);
        return result;
    }
    public static String generate6th(){
        String result=RandomNumberStringGenerator.generate(3)
                +"-"+RandomNumberStringGenerator.generate(3)
                +"-"+RandomNumberStringGenerator.generate(3);
        return result;
    }
    public static String generate5th(){
        String result=RandomNumberStringGenerator.generate(4)
                +"-"+RandomNumberStringGenerator.generate(4)
                +"-"+RandomNumberStringGenerator.generate(4)
                +"-"+RandomNumberStringGenerator.generate(4)
                +"-"+RandomNumberStringGenerator.generate(4)
                +"-"+RandomNumberStringGenerator.generate(4);
        return result;
    }
    public static String generate4th(){
        String result=RandomNumberStringGenerator.generate(4)
                +"-"+RandomNumberStringGenerator.generate(4)
                +"-"+RandomNumberStringGenerator.generate(4)
                +"-"+RandomNumberStringGenerator.generate(4);
        return result;
    }
    public static String generate3th(){
        String result=RandomNumberStringGenerator.generate(5)
                +"-"+RandomNumberStringGenerator.generate(5)
                +"-"+RandomNumberStringGenerator.generate(5)
                +"-"+RandomNumberStringGenerator.generate(5)
                +"-"+RandomNumberStringGenerator.generate(5)
                +"-"+RandomNumberStringGenerator.generate(5);
        return result;
    }
    public static String generate2th(){
        String result=RandomNumberStringGenerator.generate(5)
                +"-"+RandomNumberStringGenerator.generate(5);
        return result;
    }
    public static String generate1th(){
        String result=RandomNumberStringGenerator.generate(5);
        return result;
    }
    public static String generate0th(){
        String result=RandomNumberStringGenerator.generate(5);
        return result;
    }

    public static List<String> generateRandomList() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            String number = String.format("%02d", i);
            list.add(number);
        }
        Collections.shuffle(list);
        return list;
    }
    public static List<Integer> geneerateRandomList(){
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        return list;
    }
}
