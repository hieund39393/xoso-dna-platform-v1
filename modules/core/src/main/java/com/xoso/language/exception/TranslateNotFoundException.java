package com.xoso.language.exception;

import com.xoso.infrastructure.core.exception.AbstractPlatformException;

public class TranslateNotFoundException extends AbstractPlatformException {
    public TranslateNotFoundException() {
        super("error.msg.translate.notFound", "Translate not found");
    }
}
