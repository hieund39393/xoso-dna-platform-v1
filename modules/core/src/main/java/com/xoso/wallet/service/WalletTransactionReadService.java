package com.xoso.wallet.service;

import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.wallet.data.WalletTransactionData;

public interface WalletTransactionReadService {
    Page<WalletTransactionData> retrieveAll(SearchParameters searchParameters);
    WalletTransactionData retrieveOne(Long transactionId);
}
