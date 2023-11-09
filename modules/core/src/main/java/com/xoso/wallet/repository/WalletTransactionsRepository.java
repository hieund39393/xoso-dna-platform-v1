package com.xoso.wallet.repository;

import com.xoso.wallet.model.WalletTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletTransactionsRepository extends JpaRepository<WalletTransactions, Long>,
        JpaSpecificationExecutor<WalletTransactions> {
    @Query("select w from WalletTransactions w where w.id = ?1")
    WalletTransactions queryFindById(Long id);

    WalletTransactions findFirstByContentOrderByIdDesc(String content);
    Optional<WalletTransactions> findFirstByTransactionNoOrderByIdDesc(String content);
}
