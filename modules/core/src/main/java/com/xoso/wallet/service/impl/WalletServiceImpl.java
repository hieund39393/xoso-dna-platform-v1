package com.xoso.wallet.service.impl;

import com.xoso.infrastructure.core.exception.BadRequestException;
import com.xoso.infrastructure.core.exception.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.xoso.bank.exception.BankNotFoundException;
import com.xoso.bank.model.MasterBankAccount;
import com.xoso.bank.repository.BankRepository;
import com.xoso.bank.repository.MasterBankAccountRepository;
import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.security.service.AuthenticationService;
import com.xoso.user.model.AppUser;
import com.xoso.user.repository.AppUserRepository;
import com.xoso.wallet.data.WalletData;
import com.xoso.wallet.exception.*;
import com.xoso.wallet.model.Wallet;
import com.xoso.wallet.model.WalletStatus;
import com.xoso.wallet.repository.WalletRepository;
import com.xoso.wallet.service.WalletService;
import com.xoso.wallet.wsdto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class WalletServiceImpl implements WalletService {

    private AppUserRepository appUserRepository;
    private WalletRepository walletRepository;
    private MasterBankAccountRepository masterBankAccountRepository;
    private BankRepository bankRepository;
    private AuthenticationService authenticationService;

    @Override
    @Transactional
    public ResultBuilder create(WalletCreateWsDTO request) {

        AppUser currentUser = null;
        try {
            currentUser = authenticationService.authenticatedUser();
        } catch (Exception e) {
            log.warn("Get current user from context not found");
        }

        log.info("create wallet {}", request);
        if (nonNull(request.getBalance()) && request.getBalance().compareTo(ZERO) < 0) {
            throw new InvalidValueException("balance");
        }
        var user = appUserRepository.findById(request.getUserId())
            .orElseThrow(() -> new InvalidValueException("user"));

        var wallet = Wallet.builder()
            .user(user)
            .status(WalletStatus.ACTIVE)
            .balance(nonNull(request.getBalance()) ? request.getBalance() : ZERO)
            .totalBet(ZERO)
            .totalDeposit(ZERO)
            .totalWin(ZERO)
            .totalWithdraw(ZERO)
            .isMaster(request.isMaster())
            .build();
        wallet.setCreatedDate(LocalDateTime.now());

        currentUser = Objects.nonNull(currentUser) ? currentUser : user;

        wallet.setCreatedBy(currentUser.getUsername());
        var response = walletRepository.saveAndFlush(wallet);

        var bankAccounts = request.getBankAccounts();
        if (!CollectionUtils.isEmpty(bankAccounts)) {
            this.upsertWalletBankAccount(bankAccounts, wallet, currentUser);
        }

        return new ResultBuilder().withEntityId(response.getId()).build();
    }

    @Override
    public Wallet createWalletDefault(AppUser user) {
        var wallet = Wallet.builder()
                .user(user)
                .status(WalletStatus.ACTIVE)
                .balance(ZERO)
                .totalBet(ZERO)
                .totalDeposit(ZERO)
                .totalWin(ZERO)
                .totalWithdraw(ZERO)
                .isMaster(Boolean.FALSE)
                .build();
        wallet.setCreatedDate(LocalDateTime.now());
        wallet.setModifiedDate(LocalDateTime.now());
        walletRepository.saveAndFlush(wallet);
        return wallet;
    }

    private void upsertWalletBankAccount(List<WalletBankAccountWsDTO> bankAccounts, Wallet wallet, AppUser currentUser) {
        var walletBankAccountNews = new ArrayList<MasterBankAccount>();
        for (WalletBankAccountWsDTO bankAccount : bankAccounts) {
            var bank = this.bankRepository.findById(bankAccount.getBankId()).orElseThrow(() ->
                    new BankNotFoundException(bankAccount.getBankId()));
            var walletBankAccountUpsert = MasterBankAccount.builder()
                    .bank(bank)
                    .accountName(bankAccount.getAccountName())
                    .accountNumber(bankAccount.getAccountNumber())
                    .cardNumber(bankAccount.getCardNumber())
                    .wallet(wallet)
                    .password(bankAccount.getPassword())
                    .userName(bankAccount.getUserName())
                    .enabled(bankAccount.isEnabled())
                    .build();
            if (bankAccount.getId() != null) {
                walletBankAccountUpsert.setId(bankAccount.getId());
                walletBankAccountUpsert.setModifiedBy(currentUser.getUsername());
                walletBankAccountUpsert.setModifiedDate(LocalDateTime.now());
            } else {
                walletBankAccountUpsert.setCreatedDate(LocalDateTime.now());
                walletBankAccountUpsert.setCreatedBy(currentUser.getUsername());
            }
            walletBankAccountNews.add(walletBankAccountUpsert);
        }
        this.masterBankAccountRepository.saveAll(walletBankAccountNews);
    }

    @Override
    @Transactional
    public ResultBuilder updateWalletMaster(Long walletId, WalletUpdateWsDTO request) {

        final var currentUser = authenticationService.authenticatedUser();
        var wallet = walletRepository.findById(walletId).orElseThrow(WalletNotFountException::new);

        if (!Boolean.TRUE.equals(wallet.getIsMaster())) {
            throw new WalletIsNotMaster();
        }

        if (request.getBalance() == null || request.getBalance().compareTo(ZERO) < 0) {
            throw new InvalidValueException("balance");
        }
        wallet.setBalance(request.getBalance());
        wallet.setModifiedBy(currentUser.getUsername());
        wallet.setModifiedDate(LocalDateTime.now());
        this.walletRepository.saveAndFlush(wallet);
        var bankAccounts = request.getBankAccounts();
        if (!CollectionUtils.isEmpty(bankAccounts)) {
            this.upsertWalletBankAccount(bankAccounts, wallet, currentUser);
        }

        return new ResultBuilder().withEntityId(wallet.getId()).build();
    }

    @Override
    @Transactional
    public void updateBalance(long userId, double balance) {
        List<Wallet> wallet = walletRepository.findByUserId(userId);
        Wallet userWallet ;
        if(!wallet.isEmpty()) {
            userWallet = wallet.get(0);
            double newBalance = userWallet.getBalance().doubleValue() + balance;
            userWallet.setBalance(BigDecimal.valueOf(newBalance));
            walletRepository.saveAndFlush(userWallet);
        }
    }

    @Override
    @Transactional
    public void updateBalanceOfUserByTicket(long userId, double balance) {
        List<Wallet> wallet = walletRepository.findByUserId(userId);
        Wallet userWallet ;
        if(!wallet.isEmpty()) {
            userWallet = wallet.get(0);
            double newBalance = userWallet.getBalance().doubleValue() + balance;
            System.out.println("update balance of wallet: "+userWallet.getId() +" "+BigDecimal.valueOf(newBalance));
            if (newBalance < 0)
                throw new BadRequestException(ExceptionCode.WALLET_MONEY_NOT_ENOUGH);
            userWallet.setBalance(BigDecimal.valueOf(newBalance));
            if(balance<0){
                //mua ve
                userWallet.setTotalBet(userWallet.getTotalBet().subtract(BigDecimal.valueOf(balance)));
            }else{
                //thang cuoc
                userWallet.setTotalWin(userWallet.getTotalWin().add(BigDecimal.valueOf(balance)));
            }
            walletRepository.saveAndFlush(userWallet);
        }
    }

    @Override
    public WalletData getWalletById(long walletId) {
        return walletRepository.findById(walletId)
            .map(this::toWalletData)
            .orElseThrow(WalletNotFountException::new);
    }

    @Override
    public WalletData getWalletByUsername(String username) {
        return walletRepository.findByUsername(username)
            .map(this::toWalletData)
            .orElseThrow(WalletNotFountException::new);
    }

    @Override
    public WalletData lock(Long walletId) {
        var wallet = walletRepository.findById(walletId).orElseThrow(WalletNotFountException::new);
        if (WalletStatus.INACTIVE.equals(wallet.getStatus())) {
            throw new WalletInactive();
        }
        wallet.setStatus(WalletStatus.INACTIVE);
        this.walletRepository.saveAndFlush(wallet);
        return toWalletData(wallet);
    }

    @Override
    public WalletData unlock(Long walletId) {
        var wallet = walletRepository.findById(walletId).orElseThrow(WalletNotFountException::new);
        if (WalletStatus.ACTIVE.equals(wallet.getStatus())) {
            throw new WalletActive();
        }
        wallet.setStatus(WalletStatus.ACTIVE);
        this.walletRepository.saveAndFlush(wallet);
        return toWalletData(wallet);
    }

    private WalletData toWalletData(Wallet wallet) {
        return WalletData.builder()
            .id(wallet.getId())
            .userId(wallet.getUser().getId())
            .balance(wallet.getBalance())
                .totalBet(wallet.getTotalBet())
                .totalDeposit(wallet.getTotalDeposit())
                .totalWin(wallet.getTotalWin())
                .totalWithdraw(wallet.getTotalWithdraw())
            .isMaster(wallet.getIsMaster())
            .build();
    }
}
