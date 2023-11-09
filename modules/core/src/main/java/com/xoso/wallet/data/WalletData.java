package com.xoso.wallet.data;

import com.xoso.bank.data.ClientBankAccountData;
import com.xoso.bank.data.MasterBankAccountData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class WalletData {

    private Long id;
    private Long userId;
    private Long clientId;
    private String accountNo;
    private String mobileNo;
    private String fullName;
    private String username;
    private String nationalId;
    private BigDecimal balance;
    private Boolean isMaster;
    private String status;
    private String createdDate;

    private String bankCode;
    private String accountNumber;

    private BigDecimal totalWin;
    private BigDecimal totalBet;
    private BigDecimal totalWithdraw;
    private BigDecimal totalDeposit;

    private List<MasterBankAccountData> bankAccounts;
    private List<ClientBankAccountData> clientBankAccounts;
}
