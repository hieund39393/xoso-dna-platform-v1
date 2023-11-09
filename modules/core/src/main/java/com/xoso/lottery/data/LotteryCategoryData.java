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
public class LotteryCategoryData {
    public String categoryName;
    public List<LotteryData> lotteries;
}
