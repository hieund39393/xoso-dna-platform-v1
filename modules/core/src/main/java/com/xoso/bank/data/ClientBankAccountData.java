package com.xoso.bank.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientBankAccountData implements Serializable {
    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private String mobileNo;
    private Long bankId;
    private String bankCode;
    private String bankName;
    private String accountName;
    private String accountNumber;
    private String cardNumber;
    private boolean enabled;
}
