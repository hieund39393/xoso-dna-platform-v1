package com.xoso.lottery.data;

import com.xoso.job.RandomNumberStringGenerator;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@Builder
public class LotteryResult {
    String result0;
    String result1;
    String result2;
    String result3;
    String result4;
    String result5;
    String result6;
    String result7;
    String resultArr;

    public static LotteryResult generateRandom(){
        //giai dac biet:
        String result0 = RandomNumberStringGenerator.generate0th();
        String result1 = RandomNumberStringGenerator.generate1th();
        String result2 = RandomNumberStringGenerator.generate2th();
        String result3 = RandomNumberStringGenerator.generate3th();
        String result4 = RandomNumberStringGenerator.generate4th();
        String result5 = RandomNumberStringGenerator.generate5th();
        String result6 = RandomNumberStringGenerator.generate6th();
        String result7 = RandomNumberStringGenerator.generate7th();

        ArrayList<String> results = new ArrayList<>();
        results.addAll(Arrays.asList(result0.split("-")));
        results.addAll(Arrays.asList(result1.split("-")));
        results.addAll(Arrays.asList(result2.split("-")));
        results.addAll(Arrays.asList(result3.split("-")));
        results.addAll(Arrays.asList(result4.split("-")));
        results.addAll(Arrays.asList(result5.split("-")));
        results.addAll(Arrays.asList(result6.split("-")));
        results.addAll(Arrays.asList(result7.split("-")));

        String resultArr = getLastTwoDigits(results);
        return LotteryResult.builder()
                .result0(result0)
                .result1(result1)
                .result2(result2)
                .result3(result3)
                .result4(result4)
                .result5(result5)
                .result6(result6)
                .result7(result7)
                .resultArr(resultArr)
                .build();
    }

    public static LotteryResult generateRandom(String ddb, String dgn){
        //giai dac biet:
        String result0 = RandomNumberStringGenerator.generate(2)+ddb;
        String result1 = RandomNumberStringGenerator.generate(2)+dgn;
        String result2 = RandomNumberStringGenerator.generate2th();
        String result3 = RandomNumberStringGenerator.generate3th();
        String result4 = RandomNumberStringGenerator.generate4th();
        String result5 = RandomNumberStringGenerator.generate5th();
        String result6 = RandomNumberStringGenerator.generate6th();
        String result7 = RandomNumberStringGenerator.generate7th();

        ArrayList<String> results = new ArrayList<>();
        results.addAll(Arrays.asList(result0.split("-")));
        results.addAll(Arrays.asList(result1.split("-")));
        results.addAll(Arrays.asList(result2.split("-")));
        results.addAll(Arrays.asList(result3.split("-")));
        results.addAll(Arrays.asList(result4.split("-")));
        results.addAll(Arrays.asList(result5.split("-")));
        results.addAll(Arrays.asList(result6.split("-")));
        results.addAll(Arrays.asList(result7.split("-")));

        String resultArr = getLastTwoDigits(results);
        return LotteryResult.builder()
                .result0(result0)
                .result1(result1)
                .result2(result2)
                .result3(result3)
                .result4(result4)
                .result5(result5)
                .result6(result6)
                .result7(result7)
                .resultArr(resultArr)
                .build();
    }

    public static LotteryResult generateRandomChanLe(boolean isChan){
        //giai dac biet:
        String result0 = isChan?RandomNumberStringGenerator.generateChan(5):RandomNumberStringGenerator.generateLe(5);
        String result1 = RandomNumberStringGenerator.generate1th();
        String result2 = RandomNumberStringGenerator.generate2th();
        String result3 = RandomNumberStringGenerator.generate3th();
        String result4 = RandomNumberStringGenerator.generate4th();
        String result5 = RandomNumberStringGenerator.generate5th();
        String result6 = RandomNumberStringGenerator.generate6th();
        String result7 = RandomNumberStringGenerator.generate7th();

        ArrayList<String> results = new ArrayList<>();
        results.addAll(Arrays.asList(result0.split("-")));
        results.addAll(Arrays.asList(result1.split("-")));
        results.addAll(Arrays.asList(result2.split("-")));
        results.addAll(Arrays.asList(result3.split("-")));
        results.addAll(Arrays.asList(result4.split("-")));
        results.addAll(Arrays.asList(result5.split("-")));
        results.addAll(Arrays.asList(result6.split("-")));
        results.addAll(Arrays.asList(result7.split("-")));

        String resultArr = getLastTwoDigits(results);
        return LotteryResult.builder()
                .result0(result0)
                .result1(result1)
                .result2(result2)
                .result3(result3)
                .result4(result4)
                .result5(result5)
                .result6(result6)
                .result7(result7)
                .resultArr(resultArr)
                .build();
    }


    private static String getLastTwoDigits(ArrayList<String> numbers) {
        ArrayList<Integer> lastTwoDigits = new ArrayList<>();

        for (String number : numbers) {
            if (number.length() >= 2 && number.length() <= 6) {
                int lastTwo = Integer.parseInt(number.substring(number.length() - 2));
                lastTwoDigits.add(lastTwo);
            }
        }

        Collections.sort(lastTwoDigits);

        StringBuilder resultBuilder = new StringBuilder();
        for (int i = 0; i < lastTwoDigits.size(); i++) {
            resultBuilder.append(String.format("%02d", lastTwoDigits.get(i)));
            if (i < lastTwoDigits.size() - 1) {
                resultBuilder.append("-");
            }
        }

        return resultBuilder.toString();
    }
}
