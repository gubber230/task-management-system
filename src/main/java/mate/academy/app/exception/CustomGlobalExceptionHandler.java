package mate.academy.app.exception;

import java.time.LocalDateTime;
import mate.academy.app.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomGlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ErrorResponse> handleRegistrationException(RegistrationException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(FileOperationException.class)
    public ResponseEntity<ErrorResponse> handleFileOperationException(FileOperationException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, status);
    }
}
