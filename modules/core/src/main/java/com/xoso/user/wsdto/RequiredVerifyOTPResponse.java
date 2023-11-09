package com.xoso.user.wsdto;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString(callSuper = true)
public class RequiredVerifyOTPResponse extends AbstractUserResponse {

    private final @NonNull Long id;
    private final @NonNull String mobileNo;
    private final String code;

    public RequiredVerifyOTPResponse(long id, String mobileNo, String code) {
        super(UserAction.VERIFY_OTP);
        this.mobileNo = mobileNo;
        this.id = id;
        this.code = code;
    }
}
