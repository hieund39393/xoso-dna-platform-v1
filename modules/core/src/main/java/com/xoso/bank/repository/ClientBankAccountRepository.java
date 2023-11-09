package com.xoso.bank.repository;

import com.xoso.bank.model.Bank;
import com.xoso.bank.model.ClientBankAccount;
import com.xoso.user.model.AppUser;
import com.xoso.wallet.model.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientBankAccountRepository extends JpaRepository<ClientBankAccount, Long> {
    Page<ClientBankAccount> findByUser(AppUser user, Pageable pageable);
    ClientBankAccount findByUser(AppUser user);
    List<ClientBankAccount> findByUserAndBankAndAccountNumber(AppUser user, Bank bank, String accountNumber);

    ClientBankAccount findByBankAndAccountNumber(Bank bank, String accountNumber);
}
