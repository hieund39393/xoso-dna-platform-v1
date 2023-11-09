package com.xoso.bank.service.impl;

import com.xoso.bank.data.BankAccountData;
import com.xoso.bank.data.BankData;
import com.xoso.bank.exception.BankNotFoundException;
import com.xoso.bank.model.Bank;
import com.xoso.bank.model.ClientBankAccount;
import com.xoso.bank.repository.BankRepository;
import com.xoso.bank.repository.ClientBankAccountRepository;
import com.xoso.bank.repository.MasterBankAccountRepository;
import com.xoso.bank.service.BankService;
import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.exception.BadRequestException;
import com.xoso.infrastructure.core.exception.InvalidParamException;
import com.xoso.infrastructure.core.exception.PermissionDeniedException;
import com.xoso.user.exception.UserNotFoundException;
import com.xoso.user.model.AppUser;
import com.xoso.user.repository.AppUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class BankServiceImpl implements BankService {
    @Autowired
    BankRepository bankRepository;
    @Autowired
    ClientBankAccountRepository clientBankAccountRepository;
    @Autowired
    MasterBankAccountRepository masterBankAccountRepository;
    @Autowired
    AppUserRepository appUserRepository;
    @Override
    public Page<BankData> getListBank(String searchValue, Pageable pageable) {
        Page<Bank> banks = bankRepository.findBank(searchValue, pageable);
        return banks.map(BankData::fromEntity);
    }

    @Override
    public BankData createBank(String author, Bank bank) {
        bank.setCreatedBy(author);
        bank.setCreatedDate(LocalDateTime.now());
        Bank existedBank = bankRepository.findBankByCode(bank.getCode());
        if(existedBank != null)
            throw new InvalidParamException("Bank with code [%s] already existed",bank.getCode());
        return BankData.fromEntity(bankRepository.saveAndFlush(bank));
    }

    @Override
    public BankData getBankDetail(long id) {
        Bank bank = bankRepository.findById(id).orElseThrow( ()->new InvalidParamException("Bank [%s] not found",id));
        return BankData.fromEntity(bank);
    }

    @Override
    public BankData updateBank(String author, long id, Bank bank) {
        Bank oldBank = bankRepository.findById(id).orElseThrow( ()->new InvalidParamException("Bank with id [%s] not found",id));
        Bank existedBank = bankRepository.findBankByCode(bank.getCode());
        if(existedBank != null && existedBank.getId() != oldBank.getId())
            throw new InvalidParamException("Bank with code [%s] already existed", bank.getCode());
        oldBank.setCode(bank.getCode());
        oldBank.setName(bank.getName());
        oldBank.setDescription(bank.getDescription());
        oldBank.setEnabled(bank.isEnabled());
        oldBank.setModifiedBy(author);
        oldBank.setModifiedDate(LocalDateTime.now());
        Bank updatedBank = bankRepository.save(oldBank);
        return BankData.fromEntity(updatedBank);
    }

    @Override
    public Page<BankAccountData> findBankAccounts(String username, Pageable pageable) {
        AppUser user = appUserRepository.findAppUserByName(username);
        if(user == null)
            throw new InvalidParamException("User [%s] not found",username);
        return clientBankAccountRepository.findByUser(user, pageable).map(BankAccountData::fromEntity);
    }

    @Override
    public Page<BankAccountData> findAllMasterBankAccounts(Pageable pageable) {
        return masterBankAccountRepository.findAll(pageable).map(BankAccountData::fromEntity);

    }

    @Override
    public BankAccountData createBankAccount(String username, ClientBankAccount clientBankAccount) {
        AppUser user = appUserRepository.findAppUserByName(username);
        if(user == null)
            throw new UserNotFoundException(username);
        Bank bank = bankRepository.findById(clientBankAccount.getBankId()).orElseThrow(()->new InvalidParamException("Bank's account with id [%s] not found", clientBankAccount.getBankId()));
        clientBankAccount.setBank(bank);
        clientBankAccount.setUser(user);
        clientBankAccount.setCreatedBy(username);
        clientBankAccount.setEnabled(false);
        clientBankAccount.setCreatedDate(LocalDateTime.now());
        try {
            ClientBankAccount newBankAccount = clientBankAccountRepository.saveAndFlush(clientBankAccount);
            return BankAccountData.fromEntity(newBankAccount);
        }catch (DataIntegrityViolationException e){
            throw new InvalidParamException("Account of Bank [%s] already existed or accountNumber[%s] already existed",bank.getCode(),clientBankAccount.getAccountNumber());
        }
    }

    @Override
    public BankAccountData getBankAccount(String userName, long id) {
        ClientBankAccount clientBankAccount = clientBankAccountRepository.findById(id).orElseThrow(()->new InvalidParamException("Bank account with id [%s] not found",id));
        if(!userName.equalsIgnoreCase(clientBankAccount.getUser().getUsername()))
            throw new PermissionDeniedException("Permission denied with bank account [%s]",id);
        return BankAccountData.fromEntity(clientBankAccount);
    }
    @Override
    public BankAccountData updateBankAccount(String username, long id,  ClientBankAccount clientBankAccount) {
        ClientBankAccount oldClientBankAccount = clientBankAccountRepository.findById(id)
                .orElseThrow(()-> new InvalidParamException("Bank account with id [%s] not found",id));
        if(!username.equalsIgnoreCase(oldClientBankAccount.getUser().getUsername()))
            throw new PermissionDeniedException("Permission denied with bank account [%s]",id);

        oldClientBankAccount.setAccountName(clientBankAccount.getAccountName());
        oldClientBankAccount.setAccountNumber(clientBankAccount.getAccountNumber());
        oldClientBankAccount.setCardNumber(clientBankAccount.getCardNumber());
        oldClientBankAccount.setEnabled(false);
        oldClientBankAccount.setModifiedBy(username);
        oldClientBankAccount.setModifiedDate(LocalDateTime.now());
        try {
            ClientBankAccount newBankAccount = clientBankAccountRepository.saveAndFlush(oldClientBankAccount);
            return BankAccountData.fromEntity(newBankAccount);
        }catch (DataIntegrityViolationException e){
            throw new InvalidParamException("AccountNumber[%s] already existed",clientBankAccount.getAccountNumber());
        }
    }

    @Override
    public void deleteBankAccount(String username, long id) {
        ClientBankAccount oldClientBankAccount = clientBankAccountRepository.findById(id)
                .orElseThrow(()-> new InvalidParamException("Bank account with id [%s] not found",id));
        if(!username.equalsIgnoreCase(oldClientBankAccount.getUser().getUsername()))
            throw new PermissionDeniedException("Permission denied with bank account [%s]",id);
        clientBankAccountRepository.deleteById(id);
    }

    @Override
    public ResultBuilder approveBankAccount(Long id) {
        var clientBankAccount = clientBankAccountRepository.findById(id)
                .orElseThrow(()-> new BankNotFoundException(id));
        clientBankAccount.setEnabled(true);
        this.clientBankAccountRepository.saveAndFlush(clientBankAccount);
        return new ResultBuilder().withEntityId(clientBankAccount.getId()).build();
    }
}
