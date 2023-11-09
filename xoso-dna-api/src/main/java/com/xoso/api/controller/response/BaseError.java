package com.xoso.api.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class BaseError implements Serializable {

    private String code;
    private String message;
    private Object[] args;
    protected String trackGuid;

    public BaseError(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
