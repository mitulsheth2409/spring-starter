package com.starter.app.ws.starterdemo.exceptions;

import com.starter.app.ws.starterdemo.ui.model.response.ErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    // generic method to handle any exception
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorMessage> handleAnyException(final Exception ex, final WebRequest request) {
        final String message = ex.getLocalizedMessage() == null ? ex.toString() : ex.getLocalizedMessage();
        final ErrorMessage errorMessage = new ErrorMessage(new Date(), message);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), INTERNAL_SERVER_ERROR);
    }

    // method to handle any null pointer exception and custom exception
    // if the same exceptions are specified elsewhere, then the compiler throws ambiguous method error
    @ExceptionHandler(value = {NullPointerException.class, UserServiceException.class})
    public ResponseEntity<ErrorMessage> handleSpecificException(final NullPointerException ex, final WebRequest request) {
        final String message = ex.getLocalizedMessage() == null ? ex.toString() : ex.getLocalizedMessage();
        final ErrorMessage errorMessage = new ErrorMessage(new Date(), message);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), INTERNAL_SERVER_ERROR);
    }
}
