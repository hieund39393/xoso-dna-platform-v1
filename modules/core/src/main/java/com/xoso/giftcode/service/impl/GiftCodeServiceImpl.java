package com.xoso.giftcode.service.impl;

import com.xoso.bank.model.ClientBankAccount;
import com.xoso.bank.repository.ClientBankAccountRepository;
import com.xoso.giftcode.model.GiftCode;
import com.xoso.giftcode.repository.GiftCodeRepository;
import com.xoso.giftcode.service.GiftCodeService;
import com.xoso.infrastructure.constant.ConstantCommon;
import com.xoso.infrastructure.core.exception.BadRequestException;
import com.xoso.infrastructure.core.exception.ExceptionCode;
import com.xoso.infrastructure.security.service.AuthenticationService;
import com.xoso.user.model.AppUser;
import com.xoso.wallet.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class GiftCodeServiceImpl implements GiftCodeService {
    @Autowired
    GiftCodeRepository giftCodeRepository;
    @Autowired
    WalletService walletService;
    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    ClientBankAccountRepository clientBankAccountRepository;

    @Override
    @Transactional
    public void useGiftCode(String code) {
        //lay thong tin user
        AppUser user = authenticationService.authenticatedUser();
        Long userId = user.getId();
        //kiem tra xem user da su dung code chua
        GiftCode gifCode = giftCodeRepository.findByUserIdAndCode(userId, code).orElse(null);
        if(gifCode != null){
            throw new BadRequestException(ExceptionCode.GIFTCODE_ALREADY_USED);
        }
        ClientBankAccount clientBankAccount = clientBankAccountRepository.findByUser(user);
        if(clientBankAccount == null){
            throw new BadRequestException(ExceptionCode.BANK_ACCOUNT_DOES_NOT_EXISTED);
        }

        if(code.equalsIgnoreCase(ConstantCommon.CODE_TAN_THU)){
            //cong them tien vao vi user
            walletService.updateBalance(userId, ConstantCommon.THUONG_TAN_THU);
            GiftCode usedGiftCode = GiftCode.builder()
                    .userId(userId)
                    .code(code)
                    .build();
            usedGiftCode.setCreatedDate(LocalDateTime.now());
            usedGiftCode.setModifiedDate(LocalDateTime.now());
            usedGiftCode.setModifiedBy(user.getUsername());
            giftCodeRepository.save(usedGiftCode);
        } else
            throw new BadRequestException(ExceptionCode.GIFTCODE_INVALID);
    }
}
