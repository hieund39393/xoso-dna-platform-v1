package com.xoso.wallet.service;

import com.xoso.autobank.dto.BankTransferInfoDto;
import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.user.model.AppUser;
import com.xoso.wallet.data.DepositTemplateData;
import com.xoso.wallet.data.WalletTransactionData;
import com.xoso.wallet.wsdto.AdminWithdrawRequestWsDTO;
import com.xoso.wallet.model.WalletTransactions;
import com.xoso.wallet.wsdto.ApproveTransactionRequestWsDTO;
import com.xoso.wallet.wsdto.AdminDepositRequestWsDTO;
import com.xoso.wallet.wsdto.WithdrawRequestWsDTO;

public interface WalletTransactionService {
    DepositTemplateData depositTemplate();
    void confirmDeposit();
    ResultBuilder approveDeposit(Long transactionId, ApproveTransactionRequestWsDTO request);
    WalletTransactionData withdrawRequest(WithdrawRequestWsDTO request);
    ResultBuilder approveWithdraw(Long transactionId);
    ResultBuilder reject(Long transactionId);
    ResultBuilder depositFromAdmin(Long clientWalletId, AdminDepositRequestWsDTO request);
    ResultBuilder withdrawFromAdmin(Long clientWalletId, AdminWithdrawRequestWsDTO request);

    void approveDeposit(WalletTransactions transactions, BankTransferInfoDto bankTransferInfoDto);

    void approveDeposit(AppUser user, BankTransferInfoDto bankTransferInfoDto);
}
