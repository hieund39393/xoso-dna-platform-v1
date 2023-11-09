package com.xoso.client.data;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClientData {
    private Long id;
    private String accountNo;
    private String fullName;
    private String mobileNo;
    private String emailAddress;
    private String nationalId;
    private LocalDate dateOfBirth;
    private String status;
    private String gender;

    private Long staffId;
    private Long userId;


    private Boolean isStaff;
}
