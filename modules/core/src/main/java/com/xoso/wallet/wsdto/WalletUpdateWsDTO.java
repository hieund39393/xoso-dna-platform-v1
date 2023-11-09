package com.xoso.wallet.wsdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletUpdateWsDTO {

    @NotNull(message = "Số dư ví phải lớn hơn 0")
    @Max(value = 100000000000L, message = "Số dư ví không được phép lớn hơn 100 tỷ")
    private BigDecimal balance;

    private List<WalletBankAccountWsDTO> bankAccounts;
}
