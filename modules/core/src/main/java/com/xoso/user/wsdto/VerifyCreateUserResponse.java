package com.xoso.user.wsdto;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class VerifyCreateUserResponse extends AbstractUserResponse {

    private final @NonNull String token;

    public VerifyCreateUserResponse(String token) {
        super(UserAction.CREATE_USER);
        this.token = token;
    }
}
