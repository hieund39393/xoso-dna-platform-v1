package com.xoso.lottery.wsdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LotteryTicketWsDTO {
    private long lotteryId;
    List<TicketWsDTO> tickets;
}
