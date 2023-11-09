package com.xoso.api.exception;

import com.xoso.infrastructure.constant.ConstantCommon;
import com.xoso.infrastructure.core.exception.AbstractAppException;
import com.xoso.infrastructure.core.exception.ExceptionCode;
import com.xoso.infrastructure.language.MessageUtils;
import org.apache.commons.lang3.StringUtils;
import com.xoso.api.controller.response.BaseError;
import com.xoso.api.controller.response.BaseResponse;
import com.xoso.infrastructure.core.exception.AbstractPlatformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ExceptionHelper {

    public static Logger logger = LoggerFactory.getLogger(ExceptionHelper.class);

    @ExceptionHandler(AbstractPlatformException.class)
    public ResponseEntity<BaseResponse> handleException(AbstractPlatformException exception) {
        var uuid = MDC.get(ConstantCommon.TRACK_GUID_KEY);
        logger.error(" Exception {" + uuid + "} trace:", exception);
        var baseError = new BaseError(exception.getCode(), exception.getMessage(), exception.getArgs(), uuid);
        //this.getMessage(baseError);
        var response = new BaseResponse<>(baseError, null);
        if(exception.getCode().equalsIgnoreCase("error.msg.auth.user.not.found")){
            //session het han
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AbstractAppException.class)
    public ResponseEntity<BaseResponse> handleAppException(AbstractAppException exception) {
        var uuid = MDC.get(ConstantCommon.TRACK_GUID_KEY);
        logger.error(" Exception {" + uuid + "} trace:", exception);
        //TODO nếu đa ngôn ngữ thì chọn language tương ứng
        String code = exception.getMessage();
        var baseError = new BaseError(code, exception.getCode(), null, uuid);
        //this.getMessage(baseError);
        var response = new BaseResponse<>(baseError, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception) {
        var uuid = MDC.get(ConstantCommon.TRACK_GUID_KEY);
        logger.error(" Exception {" + uuid + "} trace:", exception);
        var baseError = new BaseError(HttpStatus.BAD_REQUEST.name(), exception.getMessage(), null, uuid);
        //this.getMessage(baseError);
        var response = new BaseResponse<>(baseError, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ AuthenticationException.class })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException exception) {
        var baseError = new BaseError("error.msg.unauthorized", "Authentication failed");
        var response = new BaseResponse<>(baseError, null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> notValid(MethodArgumentNotValidException exception) {
        var errors = exception.getAllErrors();
        var message = exception.getMessage();
        var errorCode = "error.msg.data.invalid";
        ExceptionCode code = ExceptionCode.DATA_INVALID;
        if (!CollectionUtils.isEmpty(errors)) {
            message = errors.get(0).getDefaultMessage();
            if (message.contains("username")) {
                //errorCode = "error.msg.user.username.invalid";
                code = ExceptionCode.USERNAME_INVALID;
            } else if (message.contains("password")) {
                //errorCode = "error.msg.user.password.invalid";
                code = ExceptionCode.PASSWORD_INVALID;
            }
        }
        var baseError = new BaseError(code.getCode(), code.getLoMsg());
        //this.getMessage(baseError);
        var response = new BaseResponse<>(baseError, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private void getMessage(BaseError baseError) {
        String message = null;
        if (!StringUtils.isEmpty(baseError.getCode())) {
            message = MessageUtils.message(baseError.getCode(), baseError.getArgs());
        }
        if (message == null) {
            message = baseError.getMessage();
        }
        baseError.setMessage(message);
    }
}
