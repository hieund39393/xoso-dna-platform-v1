package com.xoso.staff.data;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StaffData {

    private final Long id;
    private final String firstname;
    private final String lastname;
    private final String displayName;
    private final String mobileNo;
    private final Boolean isLoanOfficer;
    private final Boolean isActive;
    private final LocalDate joiningDate;
    private final String email;
}
