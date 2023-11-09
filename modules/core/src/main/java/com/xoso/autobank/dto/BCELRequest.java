package com.xoso.autobank.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BCELRequest {
    private String username;
    private String password;
    private String accountNumber;
    private String fromDate;
    private String toDate;
}
