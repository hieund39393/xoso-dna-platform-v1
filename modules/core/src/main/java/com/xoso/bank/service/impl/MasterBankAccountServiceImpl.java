package com.xoso.bank.service.impl;

import com.xoso.bank.exception.BankAccountNotFoundException;
import com.xoso.bank.exception.BankNotFoundException;
import com.xoso.bank.model.MasterBankAccount;
import com.xoso.bank.repository.BankRepository;
import com.xoso.bank.repository.MasterBankAccountRepository;
import com.xoso.bank.service.MasterBankAccountService;
import com.xoso.bank.wsdto.MasterBankAccountCreateWsDTO;
import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.security.service.AuthenticationService;
import com.xoso.wallet.exception.WalletNotFountException;
import com.xoso.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MasterBankAccountServiceImpl implements MasterBankAccountService {

    private final WalletRepository walletRepository;
    private final BankRepository bankRepository;
    private final MasterBankAccountRepository bankAccountRepository;
    private final AuthenticationService authenticationService;

    @Autowired
    public MasterBankAccountServiceImpl(WalletRepository walletRepository, BankRepository bankRepository,
                                        MasterBankAccountRepository bankAccountRepository, AuthenticationService authenticationService) {
        this.walletRepository = walletRepository;
        this.bankRepository = bankRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.authenticationService = authenticationService;
    }

    @Override
    @Transactional
    public ResultBuilder create(MasterBankAccountCreateWsDTO request) {

        var currentUser = authenticationService.authenticatedUser();

        var bank = this.bankRepository.findById(request.getBankId()).orElseThrow(() ->
                new BankNotFoundException(request.getBankId()));

        var wallet = this.walletRepository.findById(request.getWalletId()).orElseThrow(() ->
                new WalletNotFountException());

        var bankAccount = MasterBankAccount.builder()
                .bank(bank)
                .accountName(request.getAccountName())
                .accountNumber(request.getAccountNumber())
                .cardNumber(request.getCardNumber())
                .enabled(request.isEnabled())
                .password(request.getPasswordBankAccount())
                .userName(request.getUserName())
                .wallet(wallet).build();
        bankAccount.setCreatedBy(currentUser.getUsername());
        bankAccount.setCreatedDate(LocalDateTime.now());
        this.bankAccountRepository.saveAndFlush(bankAccount);
        return new ResultBuilder().withEntityId(bankAccount.getId()).build();
    }

    @Override
    public void delete(Long bankAccountId) {
        var bankAccount = this.bankAccountRepository.findById(bankAccountId).orElseThrow(() -> new BankAccountNotFoundException(bankAccountId));
        this.bankAccountRepository.delete(bankAccount);
    }
}
