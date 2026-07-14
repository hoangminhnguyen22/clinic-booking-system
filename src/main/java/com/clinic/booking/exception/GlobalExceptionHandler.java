package com.clinic.booking.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.clinic.booking.modules.specialty.exception.SpecialtyAlreadyExistsException;
import com.clinic.booking.modules.specialty.exception.SpecialtyNotFoundException;
import com.clinic.booking.modules.doctor.exception.DoctorNotFoundException;
import com.clinic.booking.modules.doctor.exception.DuplicateDoctorEmailException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            WebRequest request) {

        Map<String, String> validationErrors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(fieldError -> validationErrors.put(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()));

        ValidationErrorResponse response = new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                request.getDescription(false).replace("uri=", ""),
                validationErrors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(SpecialtyAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleSpecialtyAlreadyExists(
            SpecialtyAlreadyExistsException exception,
            WebRequest request) {

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                exception.getMessage(),
                request.getDescription(false).replace("uri=", ""));

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(SpecialtyNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleSpecialtyNotFound(
            SpecialtyNotFoundException exception,
            WebRequest request) {

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                exception.getMessage(),
                request.getDescription(false).replace("uri=", ""));

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleDoctorNotFound(
            DoctorNotFoundException exception,
            WebRequest request) {

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                exception.getMessage(),
                request.getDescription(false).replace("uri=", ""));

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(DuplicateDoctorEmailException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateDoctorEmail(
            DuplicateDoctorEmailException exception,
            WebRequest request) {

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                exception.getMessage(),
                request.getDescription(false).replace("uri=", ""));

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }
}
