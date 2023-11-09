package com.xoso.wallet.service.impl;

import com.xoso.autobank.dto.BankTransferInfoDto;
import com.xoso.bank.data.MasterBankAccountData;
import com.xoso.bank.exception.BankNotFoundException;
import com.xoso.bank.model.ClientBankAccount;
import com.xoso.bank.model.MasterBankAccount;
import com.xoso.bank.repository.BankRepository;
import com.xoso.bank.repository.ClientBankAccountRepository;
import com.xoso.bank.repository.MasterBankAccountRepository;
import com.xoso.bank.service.ClientBankAccountService;
import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.exception.BadRequestException;
import com.xoso.infrastructure.core.exception.ExceptionCode;
import com.xoso.infrastructure.security.service.AuthenticationService;
import com.xoso.infrastructure.security.service.PlatformPasswordEncoder;
import com.xoso.infrastructure.service.AccountNumberGenerator;
import com.xoso.job.BankJob;
import com.xoso.job.LotteryJob;
import com.xoso.telegram.service.TelegramService;
import com.xoso.user.exception.PasswordWithdrawHasNotCreatedException;
import com.xoso.user.model.AppUser;
import com.xoso.user.repository.PasswordWithdrawRepository;
import com.xoso.wallet.data.WalletTransactionData;
import com.xoso.wallet.exception.InvalidValueException;
import com.xoso.wallet.exception.PasswordWithdrawDoesNotMatchException;
import com.xoso.wallet.exception.WalletNotFountException;
import com.xoso.wallet.exception.WalletTransactionNotFountException;
import com.xoso.wallet.model.TransactionType;
import com.xoso.wallet.model.Wallet;
import com.xoso.wallet.model.WalletTransactionStatus;
import com.xoso.wallet.model.WalletTransactions;
import com.xoso.wallet.repository.WalletRepository;
import com.xoso.wallet.repository.WalletTransactionsRepository;
import com.xoso.wallet.service.WalletTransactionService;
import com.xoso.wallet.data.DepositTemplateData;
import com.xoso.wallet.wsdto.AdminDepositRequestWsDTO;
import com.xoso.wallet.wsdto.AdminWithdrawRequestWsDTO;
import com.xoso.wallet.wsdto.ApproveTransactionRequestWsDTO;
import com.xoso.wallet.wsdto.WithdrawRequestWsDTO;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class WalletTransactionServiceImpl implements WalletTransactionService {
    @Value("${apibank.content.template}")
    private String CONTENT_TEMPLATE;
    final Logger logger = LoggerFactory.getLogger(WalletTransactionServiceImpl.class);
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private MasterBankAccountRepository masterBankAccountRepository;
    @Autowired
    private AccountNumberGenerator accountNumberGenerator;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired private WalletTransactionsRepository walletTransactionsRepository;
    @Autowired private BankRepository bankRepository;
    @Autowired  private ClientBankAccountRepository clientBankAccountRepository;
    @Autowired private ClientBankAccountService clientBankAccountService;
    @Autowired private PlatformPasswordEncoder platformPasswordEncoder;
    @Autowired private PasswordWithdrawRepository passwordWithdrawRepository;
    @Autowired private RedisTemplate<String, String> redisTemplate;
    @Autowired private TelegramService telegramService;
    @Autowired private BankJob bankJob;

    @Override
    @Transactional
    public DepositTemplateData depositTemplate() {
        var currentUser = this.authenticationService.authenticatedUser();
        var wallets = this.walletRepository.findByUserId(currentUser.getId());
        if (CollectionUtils.isEmpty(wallets)) {
            throw new WalletNotFountException();
        }
        var walletMasters = this.walletRepository.findMaster();
        if (CollectionUtils.isEmpty(walletMasters)) {
            throw new WalletNotFountException();
        }
        var bankAccounts = this.masterBankAccountRepository.findActiveAccounts();
        if (CollectionUtils.isEmpty(bankAccounts)) {
            throw new BankNotFoundException(null);
        }
        long userId = currentUser.getId();
        String content = CONTENT_TEMPLATE+userId;

        return DepositTemplateData.builder()
                .transactionId(userId)
                .content(content)
                .bankAccounts(bankAccounts.stream().map(MasterBankAccountData::fromEntity).collect(Collectors.toList()))
                .build();
    }

    @Override
    public void confirmDeposit() {
//        var currentUser = this.authenticationService.authenticatedUser();
//        long userId = currentUser.getId();
//        bankJob.jobAutoDeposit(userId);
    }

    @Override
    @Transactional
    public ResultBuilder approveDeposit(Long transactionId, ApproveTransactionRequestWsDTO request) {
        var currentUser = this.authenticationService.authenticatedUser();
        var amount = request.getAmount();
        var transaction = this.walletTransactionsRepository.queryFindById(transactionId);
        if (transaction == null) {
            throw new WalletTransactionNotFountException();
        }
        if (!WalletTransactionStatus.NEW.equals(transaction.getStatus())) {
            throw new InvalidValueException("transactionStatus");
        }
        transaction.setAmount(amount);
        transaction.setContent(request.getRefNumber());
        transaction.setApprovedDate(LocalDateTime.now());
        transaction.setApprovedBy(currentUser.getUsername());
        transaction.setStatus(WalletTransactionStatus.SUCCESS);
        transaction.setModifiedDate(LocalDateTime.now());
        transaction.setModifiedBy(currentUser.getUsername());
        this.walletTransactionsRepository.save(transaction);

        var wallet = transaction.getWallet();
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setTotalDeposit(wallet.getTotalDeposit().add(amount));
        wallet.setModifiedDate(LocalDateTime.now());
        wallet.setModifiedBy(currentUser.getUsername());
        this.walletRepository.save(wallet);

        var walletMaster = transaction.getWalletMaster();
        walletMaster.setBalance(walletMaster.getBalance().subtract(amount));
        walletMaster.setModifiedDate(LocalDateTime.now());
        walletMaster.setModifiedBy(currentUser.getUsername());
        this.walletRepository.save(walletMaster);

        return new ResultBuilder().withEntityId(transactionId);
    }

    @Override
    @Transactional
    public void approveDeposit(WalletTransactions transaction, BankTransferInfoDto bankTransferInfoDto) {

        transaction.setAmount(bankTransferInfoDto.amount);
        transaction.setContent(bankTransferInfoDto.getContent());
        transaction.setApprovedDate(LocalDateTime.now());
        transaction.setApprovedBy("AUTO");
        transaction.setStatus(WalletTransactionStatus.SUCCESS);
        transaction.setModifiedDate(LocalDateTime.now());
        transaction.setModifiedBy("AUTO");
        this.walletTransactionsRepository.save(transaction);

        var wallet = transaction.getWallet();
        wallet.setBalance(wallet.getBalance().add(bankTransferInfoDto.amount));
        wallet.setTotalDeposit(wallet.getTotalDeposit().add(bankTransferInfoDto.amount));
        wallet.setModifiedDate(LocalDateTime.now());
        wallet.setModifiedBy("AUTO");
        this.walletRepository.save(wallet);

        var walletMaster = transaction.getWalletMaster();
        walletMaster.setBalance(walletMaster.getBalance().subtract(bankTransferInfoDto.amount));
        walletMaster.setModifiedDate(LocalDateTime.now());
        walletMaster.setModifiedBy("AUTO");
        this.walletRepository.save(walletMaster);
    }

    @Override
    public void approveDeposit(AppUser user, BankTransferInfoDto bankTransferInfoDto) {
        var bankAccount = new MasterBankAccount();
        var bankAccounts = this.masterBankAccountRepository.findActiveAccounts();
        if (!CollectionUtils.isEmpty(bankAccounts)) {
            bankAccount = bankAccounts.get(0);
        }

        var wallets = this.walletRepository.findByUserId(user.getId());
        Wallet userWallet = new Wallet();
        if (!CollectionUtils.isEmpty(wallets)) {
            userWallet = wallets.get(0);
        }
        Wallet masterWallet = new Wallet();
        var walletMasters = this.walletRepository.findMaster();
        if (!CollectionUtils.isEmpty(walletMasters)) {
            masterWallet = walletMasters.get(0);
        }
        var transaction = WalletTransactions.builder()
                .transactionType(TransactionType.DEPOSIT)
                .transactionNo(bankTransferInfoDto.getXref())
                .status(WalletTransactionStatus.SUCCESS)
                .amount(bankTransferInfoDto.getAmount())
                .appUser(user)
                .content(bankTransferInfoDto.getContent())
                .wallet(userWallet)
                .walletMaster(masterWallet)
                .masterBankAccount(bankAccount)
                .submittedDate(LocalDateTime.now())
                .build();
        this.walletTransactionsRepository.saveAndFlush(transaction);
        //nap tien cho user
        var wallet = transaction.getWallet();
        wallet.setBalance(wallet.getBalance().add(bankTransferInfoDto.amount));
        wallet.setTotalDeposit(wallet.getTotalDeposit().add(bankTransferInfoDto.amount));
        wallet.setModifiedDate(LocalDateTime.now());
        wallet.setModifiedBy("AUTO");
        this.walletRepository.save(wallet);

        var walletMaster = transaction.getWalletMaster();
        walletMaster.setBalance(walletMaster.getBalance().subtract(bankTransferInfoDto.amount));
        walletMaster.setModifiedDate(LocalDateTime.now());
        walletMaster.setModifiedBy("AUTO");
        this.walletRepository.save(walletMaster);
    }

    @Override
    @Transactional
    public WalletTransactionData withdrawRequest(WithdrawRequestWsDTO request) {
        var currentUser = this.authenticationService.authenticatedUser();
        //moi lan rut tien cua user  khong cach nhau it hon 30 phut
        //luu vao cache
        String redisId = "WITHDRAW_REQ_"+currentUser.getUsername();
        String value = redisTemplate.opsForValue().get(redisId);
        if(value != null && !value.isEmpty())
            throw new BadRequestException(ExceptionCode.WITHDRAW_NOT_ACCEPT_BYTIME);

        // validate password
        var passwordWithdraws = this.passwordWithdrawRepository.findByUser(currentUser);
        if (CollectionUtils.isEmpty(passwordWithdraws)) {
            throw new PasswordWithdrawHasNotCreatedException();
        }
        if (!this.platformPasswordEncoder.matches(request.getPasswordWithdraw(), passwordWithdraws.get(0).getPassword())) {
            throw new PasswordWithdrawDoesNotMatchException();
        }
        logger.info("Start withdrawRequest [username={}]", currentUser.getUsername());
        var withdrawAmount = request.getAmount();
        var userBankId = request.getBankId();
        var accountNumber = request.getAccountNumber();
        if (StringUtils.isEmpty(accountNumber)) {
            throw new InvalidValueException("accountNumber");
        }

//        var bankOpt = this.bankRepository.findById(bankId);
//        if (bankOpt.isEmpty()) {
//            throw new BankNotFoundException(bankId);
//        }
//        var bank = bankOpt.get();
        ClientBankAccount clientAccount = this.clientBankAccountRepository.findById(userBankId).orElseThrow(()->new BadRequestException(ExceptionCode.BANK_ACCOUNT_DOES_NOT_EXISTED));
//        var clientAccounts = this.clientBankAccountRepository.findByUserAndBankAndAccountNumber(currentUser, bank, accountNumber);
//        if (!CollectionUtils.isEmpty(clientAccounts)) {
//            clientAccount = clientAccounts.get(0);
//        } else {
//            clientAccount = this.clientBankAccountService.create(currentUser, bank, accountNumber, currentUser.getFullName());
//        }

        var wallets = this.walletRepository.findByUserId(currentUser.getId());
        if (CollectionUtils.isEmpty(wallets)) {
            throw new WalletNotFountException();
        }

        var wallet = wallets.get(0);
        if (withdrawAmount == null || withdrawAmount.doubleValue() == 0) {
            throw new InvalidValueException("withdrawAmount");
        }
        if (withdrawAmount.doubleValue() > wallet.getBalance().doubleValue()) {
            throw new InvalidValueException("withdrawAmount");
        }

        //chi cho rut khi Khi user đánh đủ số tiền lớn hơn hoặc bằng số tiền nạp vào mới dc phép rút
        if(wallet.getTotalBet().doubleValue()<wallet.getTotalDeposit().doubleValue()) {
            double money =((wallet.getTotalDeposit().doubleValue() - wallet.getTotalBet().doubleValue()));
            DecimalFormat decimalFormat = new DecimalFormat("#");
            String result = decimalFormat.format(money);
            throw new BadRequestException(ExceptionCode.WITHDRAW_NOT_ACCEPT_BY_BET_MONEY,result);
        }

        var walletMasters = this.walletRepository.findMaster();
        if (CollectionUtils.isEmpty(walletMasters)) {
            throw new WalletNotFountException();
        }
        var transactionNo = this.accountNumberGenerator.generateWithdrawNumber();
        var transaction = WalletTransactions.builder()
                .transactionType(TransactionType.WITHDRAW)
                .transactionNo(transactionNo)
                .status(WalletTransactionStatus.NEW)
                .amount(withdrawAmount)
                .appUser(currentUser)
                .content(transactionNo)
                .wallet(wallet)
                .walletMaster(walletMasters.get(0))
                .clientBankAccount(clientAccount)
                .submittedDate(LocalDateTime.now())
                .build();
        //tru tien ngay khi request rut tien
        wallet.setBalance(wallet.getBalance().subtract(withdrawAmount));
    //    wallet.setTotalWithdraw(wallet.getTotalWithdraw().add(amount));
        wallet.setModifiedDate(LocalDateTime.now());
        wallet.setModifiedBy(currentUser.getUsername());
        this.walletRepository.saveAndFlush(wallet);

        this.walletTransactionsRepository.saveAndFlush(transaction);

        //gui message toi telegram
        String msg = String.format("RUT TIEN \n"+"MA GD: %s \n"+"USER: %s \n"+"TIEN: %sKIP \n"+"STK: %s \n"+"TEN: %s \n"+"NH: %s \n"
                ,transactionNo
                ,currentUser.getUsername()
                ,withdrawAmount,
                clientAccount.getAccountNumber(),
                clientAccount.getAccountName(),
                clientAccount.getBank().getCode());
        telegramService.sendMessageToTelegramBot(msg);
        redisTemplate.opsForValue().set(redisId,"success",30,TimeUnit.MINUTES);
        return WalletTransactionData.converter(transaction);
    }

    @Override
    @Transactional
    public ResultBuilder approveWithdraw(Long transactionId) {
        var currentUser = this.authenticationService.authenticatedUser();
        var transaction = this.walletTransactionsRepository.queryFindById(transactionId);
        if (transaction == null) {
            throw new WalletTransactionNotFountException();
        }
        if (!WalletTransactionStatus.NEW.equals(transaction.getStatus())) {
            throw new InvalidValueException("transactionStatus");
        }
        if (!TransactionType.WITHDRAW.equals(transaction.getTransactionType())) {
            throw new InvalidValueException("transactionType");
        }
        var amount = transaction.getAmount();
        transaction.setAmount(amount);
        transaction.setApprovedDate(LocalDateTime.now());
        transaction.setApprovedBy(currentUser.getUsername());
        transaction.setStatus(WalletTransactionStatus.SUCCESS);
        transaction.setModifiedDate(LocalDateTime.now());
        transaction.setModifiedBy(currentUser.getUsername());
        this.walletTransactionsRepository.save(transaction);

        var wallet = transaction.getWallet();
    //    wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setTotalWithdraw(wallet.getTotalWithdraw().add(amount));
        wallet.setModifiedDate(LocalDateTime.now());
        wallet.setModifiedBy(currentUser.getUsername());
        this.walletRepository.save(wallet);

        var walletMaster = transaction.getWalletMaster();
        walletMaster.setBalance(walletMaster.getBalance().add(amount));
        walletMaster.setModifiedDate(LocalDateTime.now());
        walletMaster.setModifiedBy(currentUser.getUsername());
        this.walletRepository.save(walletMaster);

        return new ResultBuilder().withEntityId(transactionId);
    }

    @Override
    @Transactional
    public ResultBuilder reject(Long transactionId) {
        var currentUser = this.authenticationService.authenticatedUser();
        var transaction = this.walletTransactionsRepository.queryFindById(transactionId);
        if (transaction == null) {
            throw new WalletTransactionNotFountException();
        }
        if (WalletTransactionStatus.CANCELED.equals(transaction.getStatus())
                || WalletTransactionStatus.SUCCESS.equals(transaction.getStatus())) {
            throw new InvalidValueException("transactionStatus");
        }

        transaction.setRejectedDate(LocalDateTime.now());
        transaction.setRejectedBy(currentUser.getUsername());
        transaction.setStatus(WalletTransactionStatus.CANCELED);
        //cong lai tien cho user
        var wallet = transaction.getWallet();
        wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
        //wallet.setTotalWithdraw(wallet.getTotalWithdraw().add(amount));
        wallet.setModifiedDate(LocalDateTime.now());
        wallet.setModifiedBy(currentUser.getUsername());
        this.walletRepository.save(wallet);

        this.walletTransactionsRepository.save(transaction);
        return new ResultBuilder().withEntityId(transactionId);
    }

    @Override
    @Transactional
    public ResultBuilder depositFromAdmin(Long clientWalletId, AdminDepositRequestWsDTO request) {
        var admin = this.authenticationService.authenticatedUser();
        var clientWallet = this.walletRepository.queryFindById(clientWalletId);
        if (clientWallet == null) {
            throw new WalletNotFountException();
        }
        var clientUser = clientWallet.getUser();
        var walletsMasters = this.walletRepository.findMaster();
        if (CollectionUtils.isEmpty(walletsMasters)) {
            throw new WalletNotFountException();
        }
//        var masterBankAccounts = this.masterBankAccountRepository.findActiveAccounts();
//        if (CollectionUtils.isEmpty(masterBankAccounts)) {
//            throw new BankNotFoundException(null);
//        }
        var amount = request.getAmount();
//        var masterBankAccount = masterBankAccounts.get(0);
        var transaction = WalletTransactions.builder()
                .transactionType(TransactionType.DEPOSIT)
                .transactionNo(this.accountNumberGenerator.generateTransaction())
                .status(WalletTransactionStatus.SUCCESS)
                .amount(amount)
                .appUser(clientUser)
                .wallet(clientWallet)
                .walletMaster(walletsMasters.get(0))
//                .masterBankAccount(masterBankAccount)
                .submittedDate(LocalDateTime.now())
                .approvedBy(admin.getUsername())
                .approvedDate(LocalDateTime.now())
                .content(request.getContent())
                .build();
        transaction.setCreatedBy(admin.getUsername());
        this.walletTransactionsRepository.saveAndFlush(transaction);


        var wallet = transaction.getWallet();
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setTotalDeposit(wallet.getTotalDeposit().add(amount));
        wallet.setModifiedDate(LocalDateTime.now());
        wallet.setModifiedBy(admin.getUsername());
        this.walletRepository.save(wallet);

        var walletMaster = transaction.getWalletMaster();
        walletMaster.setBalance(walletMaster.getBalance().subtract(amount));
        walletMaster.setModifiedDate(LocalDateTime.now());
        walletMaster.setModifiedBy(admin.getUsername());
        this.walletRepository.save(walletMaster);

        return new ResultBuilder().withEntityId(transaction.getId());
    }

    @Override
    @Transactional
    public ResultBuilder withdrawFromAdmin(Long clientWalletId, AdminWithdrawRequestWsDTO request) {
        var admin = this.authenticationService.authenticatedUser();
        var clientWallet = this.walletRepository.queryFindById(clientWalletId);
        if (clientWallet == null) {
            throw new WalletNotFountException();
        }
        var clientUser = clientWallet.getUser();
        var walletsMasters = this.walletRepository.findMaster();
        if (CollectionUtils.isEmpty(walletsMasters)) {
            throw new WalletNotFountException();
        }
        var masterBankAccounts = this.masterBankAccountRepository.findActiveAccounts();
        if (CollectionUtils.isEmpty(masterBankAccounts)) {
            throw new BankNotFoundException(null);
        }
        var amount = request.getAmount();
        var masterBankAccount = masterBankAccounts.get(0);
        var transaction = WalletTransactions.builder()
                .transactionType(TransactionType.WITHDRAW)
                .transactionNo(this.accountNumberGenerator.generateTransaction())
                .status(WalletTransactionStatus.SUCCESS)
                .amount(amount)
                .appUser(clientUser)
                .wallet(clientWallet)
                .walletMaster(walletsMasters.get(0))
                .masterBankAccount(masterBankAccount)
                .submittedDate(LocalDateTime.now())
                .approvedBy(admin.getUsername())
                .approvedDate(LocalDateTime.now())
                .content(request.getContent())
                .build();
        transaction.setCreatedBy(admin.getUsername());
        this.walletTransactionsRepository.saveAndFlush(transaction);


        var wallet = transaction.getWallet();
        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setTotalWithdraw(wallet.getTotalWithdraw().add(amount));
        wallet.setModifiedDate(LocalDateTime.now());
        wallet.setModifiedBy(admin.getUsername());
        this.walletRepository.save(wallet);

        var walletMaster = transaction.getWalletMaster();
        walletMaster.setBalance(walletMaster.getBalance().add(amount));
        walletMaster.setModifiedDate(LocalDateTime.now());
        walletMaster.setModifiedBy(admin.getUsername());
        this.walletRepository.save(walletMaster);

        return new ResultBuilder().withEntityId(transaction.getId());
    }
}
