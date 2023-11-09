package com.xoso.bank.repository;

import com.xoso.bank.model.MasterBankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasterBankAccountRepository extends JpaRepository<MasterBankAccount, Long>, JpaSpecificationExecutor<MasterBankAccount> {
    @Query(value = "select m from MasterBankAccount m where m.enabled = true")
    Page<MasterBankAccount> findAllEnabledAccounts(Pageable pageable);
    @Query(value = "select m from MasterBankAccount m where m.enabled = true")
    List<MasterBankAccount> findActiveAccounts();
}
