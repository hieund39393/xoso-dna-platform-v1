package com.xoso.user.exception;

import com.xoso.infrastructure.core.exception.AbstractPlatformException;

public class PermissionNotFoundException extends AbstractPlatformException {
    public PermissionNotFoundException(final String code) {
        super("error.msg.permission.code.invalid", "Permission with Code " + code + " does not exist", code);
    }
}
