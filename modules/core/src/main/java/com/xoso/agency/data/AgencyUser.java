package com.xoso.agency.data;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AgencyUser {
    public String userName;
    public String ipAddress;
    public LocalDateTime registerDate;
    //tong nap
    public double totalDeposit;
    //tong rut
    public double totalWithdraw;
    //tong cuoc
    public double totalBet;
    //tong thắng
    public double totalWin;
    //so du
    public double balance;
}
