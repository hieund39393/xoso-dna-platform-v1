package com.xoso.bank.service;

import com.xoso.bank.wsdto.MasterBankAccountCreateWsDTO;
import com.xoso.infrastructure.core.data.ResultBuilder;

public interface MasterBankAccountService {
    ResultBuilder create(MasterBankAccountCreateWsDTO request);
    void delete(Long bankAccountId);
}
