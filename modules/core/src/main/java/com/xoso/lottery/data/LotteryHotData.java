package com.xoso.lottery.data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Builder
@Setter
@Getter
public class LotteryHotData {
    public Long id;
    public String name;
    public long startTime;
    public long nextTime;
    public long currentTime;
    public String result;
}
