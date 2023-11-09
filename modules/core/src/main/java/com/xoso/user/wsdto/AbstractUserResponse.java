package com.xoso.user.wsdto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractUserResponse implements UserResponse {

    @Getter
    private final UserAction requiredAction;
}
