package com.xoso.agency.model;

import com.xoso.bank.model.Bank;
import com.xoso.client.model.ClientStatus;
import com.xoso.infrastructure.core.model.AbstractAuditableCustom;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "agency_request")
public class AgencyRequest extends AbstractAuditableCustom {
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "bank_id", nullable = false)
    private Long bankId;

    @Column(name = "bank_account_name", nullable = false)
    private String accountName;

    @Column(name = "bank_account_number", nullable = false)
    private String accountNumber;

    @Column(name = "note")
    private String note;
    @Column(name = "status", nullable = false)
    private int status;

}
