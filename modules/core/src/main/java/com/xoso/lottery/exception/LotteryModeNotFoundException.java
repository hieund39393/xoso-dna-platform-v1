package com.xoso.lottery.exception;

import com.xoso.infrastructure.core.exception.AbstractPlatformException;

public class LotteryModeNotFoundException extends AbstractPlatformException {
    public LotteryModeNotFoundException() {
        super("error.msg.lotterymode.notFound", "Lottery mode not found");
    }
}
