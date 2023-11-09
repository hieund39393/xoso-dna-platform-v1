package com.xoso.agency.wsdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgencyRequestWsDTO {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private Long bankId;
    private String bankName;
    private String accountName;
    private String accountNumber;
    private String note;
    private int statusInt;
    private String status;
    private Integer agencyLevel;
    private String mobileNo;
}
