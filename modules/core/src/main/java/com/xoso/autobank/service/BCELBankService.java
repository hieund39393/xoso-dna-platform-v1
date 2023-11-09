package com.xoso.autobank.service;

import com.xoso.autobank.dto.BankTransferInfoDto;

import java.util.List;

public interface BCELBankService {
    public List<BankTransferInfoDto> queryTodayTransaction(String userName, String password, String accountNumber, String date) throws Exception;
}
