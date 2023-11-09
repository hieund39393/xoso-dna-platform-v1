package com.xoso.staff.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.xoso.infrastructure.core.model.AbstractAuditableCustom;
import com.xoso.staff.wsdto.StaffCreateRequestWsDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "staff", uniqueConstraints = {@UniqueConstraint(columnNames = { "mobile_no" }, name = "mobile_no_UNIQUE") })
public class Staff extends AbstractAuditableCustom  {

    @Column(name = "firstname", length = 50)
    private String firstname;

    @Column(name = "lastname", length = 50)
    private String lastname;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "national_id", length = 100)
    private String nationalId;

    @Column(name = "mobile_no", length = 50, nullable = false, unique = true)
    private String mobileNo;

    @Column(name = "email_address", length = 50, unique = true)
    private String emailAddress;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "joining_date", nullable = true)
//    @Temporal(TemporalType.DATE)
    private LocalDate joiningDate;

    public Staff(String firstname, String lastname, String mobileNo, boolean isActive, LocalDate joiningDate) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.mobileNo = mobileNo;
        this.active = isActive;
        this.joiningDate = joiningDate;
    }

    public static Staff fromRequest(StaffCreateRequestWsDTO request) {
        var isActive = true;
        if (request.getIsActive() != null && Boolean.FALSE.equals(request.getIsActive())) {
            isActive = false;
        }
        return new Staff(request.getFirstname(), request.getLastname(), request.getMobileNo(), isActive, request.getJoiningDate());
    }
}
