/**
 * Author: Vinod Jagwani
 */
package com.sse.example.exception;

import org.springframework.http.HttpStatus;

public interface ErrorPrinter {

    HttpStatus getHttpStatus();

}
