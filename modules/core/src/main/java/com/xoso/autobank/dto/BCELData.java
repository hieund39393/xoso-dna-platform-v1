package com.xoso.autobank.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
public class BCELData{
    public String txnId;
    public String requestAction;
    public Date responseDate;
    public String responseCode;
    public String responseMsg;
    public ArrayList<BCELResponseData> responseData;
}
