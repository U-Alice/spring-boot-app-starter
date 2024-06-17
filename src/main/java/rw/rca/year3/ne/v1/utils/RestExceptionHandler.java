package rw.rca.year3.ne.v1.utils;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import rw.rca.year3.ne.v1.exceptions.BadRequestException;
import rw.rca.year3.ne.v1.exceptions.ResourceNotFoundException;
import rw.rca.year3.ne.v1.payload.ErrorResponse;


import java.util.Date;
import java.util.List;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),ex.getMessage(), new Date());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), new Date());
        return ResponseEntity.badRequest().body(errorResponse);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        // Construct error message
        String errorMessage = "Validation failed for the following fields: ";
        for (FieldError fieldError : fieldErrors) {
            errorMessage += fieldError.getField() + " (" + fieldError.getDefaultMessage() + "), ";
        }
        errorMessage = errorMessage.substring(0, errorMessage.length() - 2);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage, new Date());
        return ResponseEntity.badRequest().body(errorResponse);

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException iex = (InvalidFormatException) cause;
            String fieldName = iex.getPath().get(0).getFieldName();
            String errorMessage = "Invalid value for field '" + fieldName + "': " + iex.getValue();
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage, new Date());
            return ResponseEntity.badRequest().body(errorResponse);
        } else {
            String errorMessage = ex.getMessage();
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage, new Date());
            return ResponseEntity.badRequest().body(errorResponse);

        }
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleSqlExceptions(ConstraintViolationException exception) {
        String errorMessage = exception.getMessage() + " - " + exception.getSQL() + " - " + exception.getSQLState();
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage, new Date());
        return ResponseEntity.badRequest().body(errorResponse);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAnyError(RuntimeException exception) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), new Date(),exception);
        return ResponseEntity.badRequest().body(errorResponse);
    }



}
