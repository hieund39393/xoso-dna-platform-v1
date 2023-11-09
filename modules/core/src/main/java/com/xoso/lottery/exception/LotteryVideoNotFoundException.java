package com.xoso.lottery.exception;

import com.xoso.infrastructure.core.exception.AbstractPlatformException;

public class LotteryVideoNotFoundException extends AbstractPlatformException {
    public LotteryVideoNotFoundException() {
        super("error.msg.lotteryvideo.not.found", "Lottery video not found");
    }
}
