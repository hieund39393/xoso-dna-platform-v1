package com.xoso.wallet.model;

import com.xoso.infrastructure.core.model.AbstractAuditableCustom;
import com.xoso.user.model.AppUser;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wallets")
public class Wallet extends AbstractAuditableCustom {

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "total_withdraw", nullable = false)
    private BigDecimal totalWithdraw;
    @Column(name = "total_deposit", nullable = false)
    private BigDecimal totalDeposit;
    @Column(name = "total_win", nullable = false)
    private BigDecimal totalWin;
    @Column(name = "total_bet", nullable = false)
    private BigDecimal totalBet;

    @Column(name = "is_master", nullable = false)
    private Boolean isMaster;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private WalletStatus status;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wallet", orphanRemoval = true, fetch=FetchType.LAZY)
    private List<WalletTransactions> walletTransactions = new ArrayList<>();

    @Version
    private int version;

    public Long getUserId() {
        Long userId = null;
        if (this.user != null) {
            userId = this.user.getId();
        }
        return userId;
    }
}
