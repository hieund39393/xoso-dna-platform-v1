package com.xoso.staff.wsdto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StaffCreateRequestWsDTO {
    private String firstname;
    private String lastname;
    private String mobileNo;
    private Boolean isActive;
    private LocalDate joiningDate;
}
