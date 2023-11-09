package com.xoso.client.model;

import com.xoso.client.data.ClientCreateRequestData;
import com.xoso.infrastructure.core.model.AbstractAuditableCustom;
import com.xoso.wallet.model.Wallet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "client")
public class Client extends AbstractAuditableCustom {

    @Column(name = "account_no", length = 20, unique = true)
    private String accountNo;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ClientStatus status;

    @Column(name = "activation_date", nullable = true)
    private LocalDate activationDate;

    @Column(name = "joining_date", nullable = true)
    private LocalDate joiningDate;

    @Column(name = "firstname", length = 50, nullable = true)
    private String firstname;

    @Column(name = "lastname", length = 50, nullable = true)
    private String lastname;

    @Column(name = "full_name", length = 100, nullable = true)
    private String fullName;

    @Column(name = "mobile_no", length = 50)
    private String mobileNo;

    @Column(name = "email_address", length = 50)
    private String emailAddress;

    @Column(name = "is_staff")
    private boolean isStaff;

    @Column(name = "date_of_birth", nullable = true)
    private LocalDate dateOfBirth;

    @Column(name = "gender", length = 50)
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    @Column(name = "national_id", length = 50, nullable = true)
    private String nationalId;

    public Client(String fullName, String mobileNo, String emailAddress, String nationalId) {
        this.status = ClientStatus.ACTIVE;
        this.fullName = fullName;
        this.mobileNo = mobileNo;
        this.emailAddress = emailAddress;
        this.isStaff = false;
        this.nationalId = nationalId;
    }

    public static Client fromRequest(ClientCreateRequestData clientData) {
        var fullName = clientData.getFullName();
        var mobileNo = clientData.getMobileNo();
        var email = clientData.getEmail();
        var nationalId = clientData.getNationalId();
        return new Client(fullName, mobileNo, email, nationalId);
    }
}
