package com.xoso.wallet.model;

import com.xoso.bank.model.ClientBankAccount;
import com.xoso.bank.model.MasterBankAccount;
import com.xoso.infrastructure.core.model.AbstractAuditableCustom;
import com.xoso.user.model.AppUser;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wallet_transactions")
public class WalletTransactions extends AbstractAuditableCustom {

    @Column(name = "transaction_no")
    private String transactionNo;

    @Column
    private BigDecimal amount;

    @Column
    private BigDecimal fee;

    @Column
    private String content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "wallet_master_id")
    private Wallet walletMaster;

    @ManyToOne(optional = false)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private AppUser appUser;

    @Column
    @Enumerated(EnumType.STRING)
    private WalletTransactionStatus status;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_bank_account_id")
    private ClientBankAccount clientBankAccount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "master_bank_account_id")
    private MasterBankAccount masterBankAccount;

    @Column(name = "submitted_date")
    private LocalDateTime submittedDate;

    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "rejected_date")
    private LocalDateTime rejectedDate;

    @Column(name = "rejected_by")
    private String rejectedBy;

    @Version
    private int version;
}
