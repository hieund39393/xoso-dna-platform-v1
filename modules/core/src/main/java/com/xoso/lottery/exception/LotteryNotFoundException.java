package com.xoso.lottery.exception;

import com.xoso.infrastructure.core.exception.AbstractPlatformException;

public class LotteryNotFoundException extends AbstractPlatformException {
    public LotteryNotFoundException() {
        super("error.msg.lottery.notFound", "Lottery not found");
    }
}
