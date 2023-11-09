package com.xoso.bank.service;

import com.xoso.bank.data.BankAccountData;
import com.xoso.bank.data.BankData;
import com.xoso.bank.model.Bank;
import com.xoso.bank.model.ClientBankAccount;
import com.xoso.infrastructure.core.data.ResultBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BankService {
    Page<BankData> getListBank(String searchValue, Pageable pageable);
    BankData getBankDetail(long id);

    BankData createBank(String author, Bank bank);

    BankData updateBank(String author, long id, Bank bank);


    Page<BankAccountData> findBankAccounts(String username, Pageable pageable);
    Page<BankAccountData> findAllMasterBankAccounts(Pageable pageable);

    BankAccountData getBankAccount(String username, long id);

    BankAccountData createBankAccount(String username, ClientBankAccount toEntity);

    BankAccountData updateBankAccount(String username, long id, ClientBankAccount toEntity);

    void deleteBankAccount(String username, long id);

    ResultBuilder approveBankAccount(Long id);
}
