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
import com.clinic.booking.modules.doctor_schedule.exception.InvalidScheduleTimeException;
import com.clinic.booking.modules.doctor_schedule.exception.OverlappingDoctorScheduleException;
import com.clinic.booking.modules.doctor_schedule.exception.DoctorScheduleNotFoundException;
import com.clinic.booking.modules.availability.exception.PastDateAvailabilityException;
import com.clinic.booking.modules.appointment.exception.AppointmentSlotUnavailableException;
import com.clinic.booking.modules.appointment.exception.AppointmentStatusTargetNotAllowedException;
import com.clinic.booking.modules.appointment.exception.AppointmentStatusUpdateBeforeStartTimeException;
import com.clinic.booking.modules.appointment.exception.AppointmentStatusUpdateNotAllowedException;
import com.clinic.booking.modules.appointment.exception.AppointmentCancellationDeadlinePassedException;
import com.clinic.booking.modules.appointment.exception.AppointmentCancellationNotAllowedException;
import com.clinic.booking.modules.appointment.exception.AppointmentInPastException;
import com.clinic.booking.modules.appointment.exception.AppointmentNotFoundException;

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

    @ExceptionHandler(InvalidScheduleTimeException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidScheduleTime(
            InvalidScheduleTimeException exception,
            WebRequest request) {

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage(),
                request.getDescription(false).replace("uri=", ""));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(OverlappingDoctorScheduleException.class)
    public ResponseEntity<ApiErrorResponse> handleOverlappingDoctorSchedule(
            OverlappingDoctorScheduleException exception,
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

    @ExceptionHandler(DoctorScheduleNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleDoctorScheduleNotFound(
            DoctorScheduleNotFoundException exception,
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

    @ExceptionHandler(PastDateAvailabilityException.class)
    public ResponseEntity<ApiErrorResponse> handlePastDateAvailability(
            PastDateAvailabilityException exception,
            WebRequest request) {

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage(),
                request.getDescription(false).replace("uri=", ""));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(AppointmentSlotUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> handleAppointmentSlotUnavailable(
            AppointmentSlotUnavailableException exception,
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

    @ExceptionHandler(AppointmentInPastException.class)
    public ResponseEntity<ApiErrorResponse> handleAppointmentInPast(
            AppointmentInPastException exception,
            WebRequest request) {

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage(),
                request.getDescription(false).replace("uri=", ""));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAppointmentNotFound(
            AppointmentNotFoundException exception,
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

    @ExceptionHandler(AppointmentCancellationNotAllowedException.class)
    public ResponseEntity<ApiErrorResponse> handleAppointmentCancellationNotAllowed(
            AppointmentCancellationNotAllowedException exception,
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

    @ExceptionHandler(AppointmentCancellationDeadlinePassedException.class)
    public ResponseEntity<ApiErrorResponse> handleAppointmentCancellationDeadlinePassed(
            AppointmentCancellationDeadlinePassedException exception,
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

    @ExceptionHandler(AppointmentStatusUpdateBeforeStartTimeException.class)
    public ResponseEntity<ApiErrorResponse> handleAppointmentStatusUpdateBeforeStartTime(
            AppointmentStatusUpdateBeforeStartTimeException exception,
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

    @ExceptionHandler(AppointmentStatusUpdateNotAllowedException.class)
    public ResponseEntity<ApiErrorResponse> handleAppointmentStatusUpdateNotAllowed(
            AppointmentStatusUpdateNotAllowedException exception,
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

    @ExceptionHandler(AppointmentStatusTargetNotAllowedException.class)
    public ResponseEntity<ApiErrorResponse> handleAppointmentStatusTargetNotAllowed(
            AppointmentStatusTargetNotAllowedException exception,
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
