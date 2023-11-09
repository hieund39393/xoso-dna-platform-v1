package com.xoso.lottery.wsdto;

import com.xoso.lottery.model.ModeCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketWsDTO {
    private String number;
    private int quantity;
    private ModeCode lotteryMode;
}
