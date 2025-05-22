package com.n1netails.n1netails.api.exception;

import com.n1netails.n1netails.api.exception.type.EmailExistException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.response.HttpErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ExceptionController implements ErrorController {

    /**
     * Not Found Exception (400)
     * @param exception not found exception
     * @return http error response
     */
    @ExceptionHandler({
            UserNotFoundException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<HttpErrorResponse> notFoundException(UserNotFoundException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(NOT_FOUND, exception.getMessage());
    }

    /**
     * Conflict Exception (409)
     * @param exception conflict exception
     * @return http error response
     */
    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<HttpErrorResponse> conflictException(EmailExistException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(CONFLICT, exception.getMessage());
    }

    /**
     * Internal Server Error Exception (500)
     * @param exception internal server error exception
     * @return http error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpErrorResponse> internalServerErrorException(EmailExistException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    private ResponseEntity<HttpErrorResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        HttpErrorResponse httpResponse = new HttpErrorResponse(
                httpStatus.value(),
                httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(),
                message);

        return new ResponseEntity<>(httpResponse, httpStatus);
    }
}
