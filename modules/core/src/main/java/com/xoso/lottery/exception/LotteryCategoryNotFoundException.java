package com.xoso.lottery.exception;

import com.xoso.infrastructure.core.exception.AbstractPlatformException;

public class LotteryCategoryNotFoundException extends AbstractPlatformException {
    public LotteryCategoryNotFoundException() {
        super("error.msg.lotterycategory.notFound", "Lottery category not found");
    }
}
