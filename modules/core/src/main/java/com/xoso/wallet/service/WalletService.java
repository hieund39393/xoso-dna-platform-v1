package com.xoso.wallet.service;

import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.user.model.AppUser;
import com.xoso.wallet.data.WalletData;
import com.xoso.wallet.model.Wallet;
import com.xoso.wallet.wsdto.WalletCreateWsDTO;
import com.xoso.wallet.wsdto.WalletUpdateWsDTO;

import java.math.BigDecimal;

public interface WalletService {

    ResultBuilder create(WalletCreateWsDTO request);
    Wallet createWalletDefault(AppUser user);
    ResultBuilder updateWalletMaster(Long walletId, WalletUpdateWsDTO request);

    void updateBalance(long userId, double balance);
    void updateBalanceOfUserByTicket(long userId, double balance);
    WalletData getWalletById(long walletId);

    WalletData getWalletByUsername(String username);

    WalletData lock(Long walletId);
    WalletData unlock(Long walletId);
}
