package com.xoso.wallet.repository;

import com.xoso.wallet.exception.WalletNotFountException;
import com.xoso.wallet.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WalletRepositoryWrapper {
    private final WalletRepository repository;

    @Autowired
    public WalletRepositoryWrapper(WalletRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly=true)
    public Wallet findOneWithNotFoundDetection(final Long id) {
        final Wallet wallet = this.repository.findById(id)
                .orElseThrow(() -> new WalletNotFountException());
        return wallet;
    }

    @Transactional
    public Wallet saveAndFlush(final Wallet wallet) {
        return this.repository.saveAndFlush(wallet) ;
    }

    @Transactional
    public Wallet save(final Wallet wallet) {
        return this.repository.save(wallet) ;
    }

    public List<Wallet> save(List<Wallet> wallets) {
        return this.repository.saveAll(wallets) ;
    }
    public void flush() {
        this.repository.flush();
    }

    @Transactional(readOnly=true)
    public List<Wallet> findByUserId(@Param("userId") Long userId) {
        return this.repository.findByUserId(userId);
    }

}
