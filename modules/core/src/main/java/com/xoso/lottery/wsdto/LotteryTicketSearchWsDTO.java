package com.xoso.lottery.wsdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LotteryTicketSearchWsDTO {
    public Long id;
    public Long userId;
    public String username;
    public String fullName;
    public String mobileNo;
    public String lotteryName;
    public String lotteryCode;
    public String sessionStatus;
    public String modeCode;
    public String numbers;
    private int quantity;
    private long price;
    private long prizeMoney;
    private boolean win;
    private int status;
    private String createdDate;
}
