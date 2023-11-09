package com.xoso.wallet.repository;

import com.xoso.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long>, JpaSpecificationExecutor<Wallet> {

    @Query("select w from Wallet w where w.id = ?1")
    Wallet queryFindById(Long id);

    @Query("select w from Wallet w where w.user.username = ?1")
    Optional<Wallet> findByUsername(String username);

    @Query("select w from Wallet w where w.user.id = ?1 and w.status = 'ACTIVE'")
    List<Wallet> findByUserId(Long userId);

    @Query("select w from Wallet w where w.isMaster = true and w.status = 'ACTIVE'")
    List<Wallet> findMaster();

}
