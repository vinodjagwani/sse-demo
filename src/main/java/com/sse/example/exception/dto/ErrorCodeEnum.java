/**
 * Author: Vinod Jagwani
 */
package com.sse.example.exception.dto;

import com.sse.example.exception.ErrorPrinter;
import org.springframework.http.HttpStatus;

public enum ErrorCodeEnum implements ErrorPrinter {

    INVALID_PARAM(HttpStatus.BAD_REQUEST);

    ErrorCodeEnum(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    private final HttpStatus httpStatus;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
