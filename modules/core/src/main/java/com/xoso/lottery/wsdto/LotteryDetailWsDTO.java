package com.xoso.lottery.wsdto;

import com.xoso.lottery.model.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LotteryDetailWsDTO {

    private Long id;
    private String name;
    private List<String> modes;
    private SessionStatus status;
    private Long startTime;

}
