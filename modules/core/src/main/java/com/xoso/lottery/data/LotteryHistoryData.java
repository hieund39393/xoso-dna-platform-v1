package com.xoso.lottery.data;

import com.xoso.lottery.model.LotterySession;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Data
@Builder
@Setter
@Getter
public class LotteryHistoryData {
    public long startTime;
    private String result0;
    private String result1;
    private String result2;
    private String result3;
    private String result4;
    private String result5;
    private String result6;
    private String result7;

    public static LotteryHistoryData fromEntity(LotterySession entity){
        return LotteryHistoryData.builder()
                .startTime(entity.getStartTime())
                .result0(entity.getResult_0())
                .result1(entity.getResult_1())
                .result2(entity.getResult_2())
                .result3(entity.getResult_3())
                .result4(entity.getResult_4())
                .result5(entity.getResult_5())
                .result6(entity.getResult_6())
                .result7(entity.getResult_7())
                .build();
    }
}
