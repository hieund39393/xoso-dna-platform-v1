package com.xoso.bank.model;

import com.xoso.infrastructure.core.model.AbstractAuditableCustom;
import com.xoso.user.model.AppUser;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "client_bank_account", indexes = {
        @Index(name = "idx_account_number", columnList = "account_number")
})
public class ClientBankAccount extends AbstractAuditableCustom {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(name = "account_name", nullable = false)
    private String accountName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "card_number")
    private String cardNumber;

    @Column
    private boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id")
    private Bank bank;

    @Transient
    private Long bankId;

    @Column
    private boolean deleted;
}
