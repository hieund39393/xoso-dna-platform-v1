package com.xoso.lottery.exception;

import com.xoso.infrastructure.core.exception.AbstractPlatformException;

public class CommandSourceNotFoundException extends AbstractPlatformException {
    public CommandSourceNotFoundException() {
        super("error.msg.command.source.not.found", "CommandSource not found");
    }
}
