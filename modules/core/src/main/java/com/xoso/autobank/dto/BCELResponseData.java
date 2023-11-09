package com.xoso.autobank.dto;

import lombok.Data;

@Data
public class BCELResponseData{
    public String dateAndTimeTransfer;
    public String xref;
    public String transferDesc;
    public double amount;
    public double balance;
    public String txnId;
    public String isDebitCredit;
    public String txnGroup;
    public String amountDisp;
    public String txnMonth;
    public int ord;
}
