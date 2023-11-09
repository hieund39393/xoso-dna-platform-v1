package com.xoso.bank.service.impl;

import com.xoso.bank.exception.BankAccountNotFoundException;
import com.xoso.bank.exception.BankNotFoundException;
import com.xoso.bank.model.Bank;
import com.xoso.bank.model.ClientBankAccount;
import com.xoso.bank.repository.BankRepository;
import com.xoso.bank.repository.ClientBankAccountRepository;
import com.xoso.bank.service.ClientBankAccountService;
import com.xoso.bank.wsdto.ClientBankAccountCreateWsDTO;
import com.xoso.bank.wsdto.ClientBankAccountUpdateWsDTO;
import com.xoso.bank.wsdto.WalletClientUpdateWsDTO;
import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.exception.BadRequestException;
import com.xoso.infrastructure.core.exception.ExceptionCode;
import com.xoso.infrastructure.security.service.AuthenticationService;
import com.xoso.user.model.AppUser;
import com.xoso.user.repository.AppUserRepository;
import com.xoso.wallet.exception.WalletNotFountException;
import com.xoso.wallet.repository.WalletRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClientBankAccountServiceImpl implements ClientBankAccountService {

    private final ClientBankAccountRepository clientBankAccountRepository;

    private final AuthenticationService authenticationService;
    private final BankRepository bankRepository;
    private final AppUserRepository appUserRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public ClientBankAccountServiceImpl(ClientBankAccountRepository clientBankAccountRepository,
                                        AuthenticationService authenticationService,
                                        BankRepository bankRepository, AppUserRepository appUserRepository,
                                        WalletRepository walletRepository) {
        this.clientBankAccountRepository = clientBankAccountRepository;
        this.authenticationService = authenticationService;
        this.bankRepository = bankRepository;
        this.appUserRepository = appUserRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    public ClientBankAccount create(AppUser user, Bank bank, String accountNumber, String accountName) {
        var clientBankAccount = ClientBankAccount.builder()
                .user(user)
                .accountName(accountName)
                .accountNumber(accountNumber)
                .bank(bank)
                .enabled(Boolean.TRUE)
                .build();
        clientBankAccount.setCreatedDate(LocalDateTime.now());
        clientBankAccount.setModifiedDate(LocalDateTime.now());
        this.clientBankAccountRepository.saveAndFlush(clientBankAccount);
        return clientBankAccount;
    }

    @Override
    @Transactional
    public ResultBuilder create(ClientBankAccountCreateWsDTO request) {
        var currentUser = authenticationService.authenticatedUser();
        var appUser = this.appUserRepository.findAppUserByName(currentUser.getUsername());
        var bank = this.bankRepository.findById(request.getBankId()).orElseThrow(() ->
                new BankNotFoundException(request.getBankId()));
        //kiem tra xem user da co tai khoan bank account chua
        ClientBankAccount existedBanks = clientBankAccountRepository.findByUser(appUser);
        if(existedBanks != null && existedBanks.isEnabled() && !existedBanks.isDeleted()){
            throw new BadRequestException(ExceptionCode.BANK_ACCOUNT_ALREADY_EXISTED);
        }
        //kiem tra bank & bankNumber da thuoc tai khoan nao khac hay chua
        ClientBankAccount clientBanks = clientBankAccountRepository.findByBankAndAccountNumber(bank,request.getAccountNumber());
        if(clientBanks != null && clientBanks.getUser().getId() != appUser.getId())
            throw new BadRequestException(ExceptionCode.BANK_ACCOUNT_ALREADY_EXISTED);
        var clientBankAccount = create(appUser, bank, request.getAccountNumber(), request.getAccountName());
        return  ResultBuilder.builder().entityId((clientBankAccount.getId())).build();
    }

    @Override
    @Transactional
    public ResultBuilder createForWallet(Long walletId, ClientBankAccountCreateWsDTO request) {
        var wallet = this.walletRepository.queryFindById(request.getWalletId());
        if (wallet == null) {
            throw new WalletNotFountException();
        }
        var appUser = wallet.getUser();
        var bank = this.bankRepository.findById(request.getBankId()).orElseThrow(() ->
                new BankNotFoundException(request.getBankId()));
        var clientBankAccount = create(appUser, bank, request.getAccountNumber(), request.getAccountName());
        if (!request.isEnabled()) {
            clientBankAccount.setEnabled(Boolean.FALSE);
        }
        if (StringUtils.isNotBlank(request.getCardNumber())) {
            clientBankAccount.setCardNumber(request.getCardNumber());
        }
        return  ResultBuilder.builder().entityId((clientBankAccount.getId())).build();
    }

    @Override
    @Transactional
    public void updateForWallet(WalletClientUpdateWsDTO request) {
        var bankAccountWsDTOs = request.getBankAccounts();
        if (CollectionUtils.isEmpty(bankAccountWsDTOs)) {
            return;
        }
        var bankAccounts = new ArrayList<ClientBankAccount>();
        for (var bankAccountWsDTO : bankAccountWsDTOs) {
            var bankAccount = this.clientBankAccountRepository.findById(bankAccountWsDTO.getId())
                    .orElseThrow(() -> new BankAccountNotFoundException(bankAccountWsDTO.getId()));
            boolean change = false;
            if (bankAccountWsDTO.getBankId() != null && !bankAccount.getBank().getId().equals(bankAccountWsDTO.getBankId())) {
                change = true;
                var bank = this.bankRepository.findById(bankAccountWsDTO.getBankId()).orElseThrow(() -> new BankNotFoundException(bankAccountWsDTO.getBankId()));
                bankAccount.setBank(bank);
            }
            if (!bankAccount.getAccountName().equals(bankAccountWsDTO.getAccountName())) {
                change = true;
                bankAccount.setAccountName(bankAccountWsDTO.getAccountName());
            }
            if (StringUtils.isNotBlank(bankAccountWsDTO.getAccountNumber()) && !bankAccount.getAccountNumber().equals(bankAccountWsDTO.getAccountNumber())) {
                change = true;
                bankAccount.setAccountNumber(bankAccountWsDTO.getAccountNumber());
            }
            if (StringUtils.isNotBlank(bankAccountWsDTO.getCardNumber())) {
                change = true;
                bankAccount.setCardNumber(bankAccountWsDTO.getCardNumber());
            }
            if (bankAccount.isEnabled() != bankAccountWsDTO.isEnabled()) {
                change = true;
                bankAccount.setEnabled(bankAccountWsDTO.isEnabled());
            }
            if (change) {
                bankAccounts.add(bankAccount);
            }
        }
        if (!CollectionUtils.isEmpty(bankAccounts)) {
            this.clientBankAccountRepository.saveAll(bankAccounts);
        }
    }

    @Override
    public void delete(Long bankAccountId) {
        var bankAccount = this.clientBankAccountRepository.findById(bankAccountId).orElseThrow(() ->
                new BankAccountNotFoundException(bankAccountId));
        bankAccount.setDeleted(true);
        this.clientBankAccountRepository.save(bankAccount);
    }

    @Override
    public void updateBankAccount(AppUser appUser, Bank bank, String accountName, String accountNumber) {
        //cap nhat du lieu bank account
        ClientBankAccount bankAccount = clientBankAccountRepository.findByUser(appUser);
        ClientBankAccount newBankAccount = ClientBankAccount.builder()
                .user(appUser)
                .bank(bank)
                .accountName(accountName)
                .accountNumber(accountNumber)
                .deleted(false)
                .enabled(true)
                .build();
        if(bankAccount != null)
            newBankAccount.setId(bankAccount.getId());
        this.clientBankAccountRepository.saveAndFlush(newBankAccount);
    }
}
