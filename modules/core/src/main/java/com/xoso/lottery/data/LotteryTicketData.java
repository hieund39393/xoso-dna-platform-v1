package com.xoso.lottery.data;

import com.xoso.lottery.model.Lottery;
import com.xoso.lottery.model.LotteryTicket;
import com.xoso.lottery.model.ModeCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LotteryTicketData {
    private String number;
    private int quantity;
    private int status;
    private double price;
    private double prize;
    private ModeCode lotteryMode;

    private String createdBy;
    private long createdDate;
    private long modifiedDate;

    public static LotteryTicketData fromEntity(LotteryTicket entity){

        long createTs =0;
        long modifyTs =0;
        if(entity.getCreatedDate() != null)
            createTs = entity.getCreatedDate().toInstant(ZoneOffset.UTC).toEpochMilli();
        if(entity.getModifiedDate() != null)
            modifyTs = entity.getModifiedDate().toInstant(ZoneOffset.UTC).toEpochMilli();
        return LotteryTicketData.builder()
                .number(entity.getNumbers())
                .quantity(entity.getQuantity())
                .lotteryMode(entity.getCode())
                .status(entity.getStatus())
                .price(entity.getPrice())
                .prize(entity.getPrizeMoney())
                .createdBy(entity.getCreatedBy())
                .createdDate(createTs)
                .modifiedDate(modifyTs)
                .build();
    }
}
