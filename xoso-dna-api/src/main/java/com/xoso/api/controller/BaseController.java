package com.xoso.api.controller;

import com.xoso.api.controller.response.BaseError;
import com.xoso.api.controller.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class BaseController {

    public ResponseEntity<BaseResponse> response(Object obj) {
        if (Objects.isNull(obj)) {
            var error = new BaseError("error.msg.response.null", "Response null");
            return new ResponseEntity<>(new BaseResponse(error, null), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(new BaseResponse(null, obj), HttpStatus.OK);
        }
    }

    public ResponseEntity<BaseResponse> responseOk(Object obj) {
        return new ResponseEntity<>(new BaseResponse(null, obj), HttpStatus.OK);
    }
}
