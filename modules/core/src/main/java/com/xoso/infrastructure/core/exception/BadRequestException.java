package com.xoso.infrastructure.core.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends AbstractAppException{

//    public BadRequestException(String code ){
//        super(code,code, null);
//    }
    public BadRequestException(ExceptionCode code){
        super(code);
    }
    public BadRequestException(ExceptionCode code, Object... args){
        super(code,args);
    }
}
