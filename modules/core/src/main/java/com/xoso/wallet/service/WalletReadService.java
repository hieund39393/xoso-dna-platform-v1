package com.xoso.wallet.service;


import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.wallet.data.WalletData;

public interface WalletReadService {

    Page<WalletData> retrieveAll(SearchParameters searchParameters, boolean isMaster);
    WalletData retrieveOne(Long walletId, boolean addBankAccounts);
}
