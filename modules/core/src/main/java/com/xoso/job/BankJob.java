package com.xoso.job;

import com.xoso.autobank.dto.BankTransferInfoDto;
import com.xoso.autobank.service.BCELBankService;
import com.xoso.bank.exception.BankNotFoundException;
import com.xoso.bank.model.MasterBankAccount;
import com.xoso.bank.repository.MasterBankAccountRepository;
import com.xoso.telegram.service.TelegramService;
import com.xoso.user.model.AppUser;
import com.xoso.user.repository.AppUserRepository;
import com.xoso.wallet.exception.WalletNotFountException;
import com.xoso.wallet.model.TransactionType;
import com.xoso.wallet.model.Wallet;
import com.xoso.wallet.model.WalletTransactionStatus;
import com.xoso.wallet.model.WalletTransactions;
import com.xoso.wallet.repository.WalletRepository;
import com.xoso.wallet.repository.WalletTransactionsRepository;
import com.xoso.wallet.service.WalletTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Component
public class BankJob {
    Logger logger = LoggerFactory.getLogger(BankJob.class);
    @Autowired
    MasterBankAccountRepository masterBankAccountRepository;
    @Autowired
    BCELBankService bcelBankService;
    @Autowired
    WalletTransactionsRepository walletTransactionsRepository;
    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    WalletRepository walletRepository;
    @Autowired
    WalletTransactionService walletTransactionService;
    @Autowired
    TelegramService telegramService;
    @Value("${apibank.content.template}")
    private String CONTENT_TEMPLATE;
    @Transactional
    public void jobAutoDeposit(Long userId) {
        List<MasterBankAccount> masterBankAccounts = masterBankAccountRepository.findAll();
        String date =getCurrDate();
        for(MasterBankAccount masterBankAccount: masterBankAccounts){
            if(masterBankAccount.isEnabled()) {
                List<BankTransferInfoDto> bankTransferInfoDtos = null;
                try {
                    bankTransferInfoDtos = bcelBankService.queryTodayTransaction(masterBankAccount.getUserName(), masterBankAccount.getPassword(), masterBankAccount.getAccountNumber(), date);
                } catch (Exception e) {
                    //throw new RuntimeException(e);
                    //gui thong tin cho admin qua telegram
                    masterBankAccount.setEnabled(false);
                    masterBankAccountRepository.save(masterBankAccount);
                    telegramService.sendMessageToTelegramBot(e.getMessage());
                    continue;
                }
                bankTransferInfoDtos.forEach(bankTransferInfoDto -> {
                    logger.info(bankTransferInfoDto.toString());
                    String content = bankTransferInfoDto.getContent();
                    //kiem tra xem content co dung format khong
                    if (content.startsWith(CONTENT_TEMPLATE) && bankTransferInfoDto.getAmount().longValue() > 0) {
                        long userIdInRequest = 0;
                        try {
                            String userIdStr = content.substring(CONTENT_TEMPLATE.length());
                            userIdInRequest = Long.parseLong(userIdStr);
                        } catch (Exception e) {
                        }
                        if(userId != null && userId != 0){
                            if (userIdInRequest == userId.longValue()) {
                                //tim trong danh sach transaction
                                WalletTransactions transactions = walletTransactionsRepository.findFirstByTransactionNoOrderByIdDesc(bankTransferInfoDto.getXref()).orElse(null);
                                if (transactions == null) {
                                    //chua co transaction nao thi tao transaction moi
                                    AppUser user = appUserRepository.queryFindAppUserById(userId);
                                    if (user != null) {
                                        walletTransactionService.approveDeposit(user, bankTransferInfoDto);
                                    }
                                }
                            }
                        } else {
                            if (userIdInRequest != 0) {
                                //tim trong danh sach transaction
                                WalletTransactions transactions = walletTransactionsRepository.findFirstByTransactionNoOrderByIdDesc(bankTransferInfoDto.getXref()).orElse(null);
                                if (transactions == null) {
                                    //chua co transaction nao thi tao transaction moi
                                    AppUser user = appUserRepository.queryFindAppUserById(userId);
                                    if (user != null) {
                                        walletTransactionService.approveDeposit(user, bankTransferInfoDto);
                                    }
                                }
                            }
                        }
//                    WalletTransactions transactions = walletTransactionsRepository.findFirstByContentOrderByIdDesc(content);
//                    if (transactions != null && transactions.getStatus() == WalletTransactionStatus.NEW) {
//                        //cap nhat tien vao cho user
//                        walletTransactionService.approveDeposit(transactions, bankTransferInfoDto);
//                    }
                    }

                });
            }
        }
    }

    private String getCurrDate(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
}
