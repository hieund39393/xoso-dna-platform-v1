package com.xoso.wallet.data;

import com.xoso.bank.data.BankAccountData;
import com.xoso.bank.data.MasterBankAccountData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepositTemplateData {

    private Long transactionId;
    private String content;
    private String status;
//    private String bankName;
//    private String accountName;
//    private String accountNumber;
    private List<MasterBankAccountData> bankAccounts;
}
