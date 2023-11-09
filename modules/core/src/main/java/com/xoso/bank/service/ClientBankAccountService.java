package com.xoso.bank.service;

import com.xoso.bank.model.Bank;
import com.xoso.bank.model.ClientBankAccount;
import com.xoso.bank.wsdto.ClientBankAccountCreateWsDTO;
import com.xoso.bank.wsdto.ClientBankAccountUpdateWsDTO;
import com.xoso.bank.wsdto.WalletClientUpdateWsDTO;
import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.user.model.AppUser;

import java.util.List;

public interface ClientBankAccountService {
    ClientBankAccount create(AppUser user, Bank bank, String bankAccount, String accountName);
    ResultBuilder create(ClientBankAccountCreateWsDTO request);
    ResultBuilder createForWallet(Long walletId, ClientBankAccountCreateWsDTO request);
    void updateForWallet(WalletClientUpdateWsDTO request);
    void delete(Long bankAccountId);

    void updateBankAccount(AppUser appUser, Bank bankId, String accountName, String accountNumber);
}
