package com.xoso.bank.service;


import com.xoso.bank.data.ClientBankAccountData;
import com.xoso.bank.wsdto.WalletClientUpdateWsDTO;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.Page;

import java.util.List;

public interface ClientBankAccountReadService {
    Page<ClientBankAccountData> retrieveAll(SearchParameters searchParameters);
    ClientBankAccountData retrieveOne(Long bankAccountId);
    List<ClientBankAccountData> retrieveByUser();
    WalletClientUpdateWsDTO retrieveByWallet(Long walletId);
    List<ClientBankAccountData> retrieveAllByWallet(Long walletId);
}
