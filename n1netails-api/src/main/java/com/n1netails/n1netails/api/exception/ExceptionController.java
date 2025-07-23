package com.n1netails.n1netails.api.exception;

import com.n1netails.n1netails.api.exception.type.*;
import com.n1netails.n1netails.api.model.response.HttpErrorResponse;
import com.yubico.webauthn.data.exception.Base64UrlException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ExceptionController implements ErrorController {

    /**
     * Bad Request Exception (400)
     * @param exception bad request exception
     * @return http error response
     */
    @ExceptionHandler({
            PasswordRegexException.class,
            Base64UrlException.class
    })
    public ResponseEntity<HttpErrorResponse> badRequestException(Exception exception) {
        log.error(exception.getMessage());
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    /**
     * Unauthorized Exception (401)
     * @param exception unauthorized exception
     * @return http error response
     */
    @ExceptionHandler({
            AccessDeniedException.class
    })
    public ResponseEntity<HttpErrorResponse> unauthorizedException(Exception exception) {
        log.error(exception.getMessage());
        return createHttpResponse(UNAUTHORIZED, exception.getMessage());
    }

    /**
     * Not Found Exception (404)
     * @param exception not found exception
     * @return http error response
     */
    @ExceptionHandler({
            N1neTokenNotFoundException.class,
            InvalidRoleException.class,
            TailNotFoundException.class,
            UserNotFoundException.class,
            NoteNotFoundException.class,
            OrganizationNotFoundException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<HttpErrorResponse> notFoundException(Exception exception) {
        log.error(exception.getMessage());
        return createHttpResponse(NOT_FOUND, exception.getMessage());
    }

    /**
     * Conflict Exception (409)
     * @param exception conflict exception
     * @return http error response
     */
    @ExceptionHandler({
            EmailExistException.class,
            N1NoteAlreadyExistsException.class
    })
    public ResponseEntity<HttpErrorResponse> conflictException(Exception exception) {
        log.error(exception.getMessage());
        return createHttpResponse(CONFLICT, exception.getMessage());
    }

    /**
     * Internal Server Error Exception (500)
     * @param exception internal server error exception
     * @return http error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpErrorResponse> internalServerErrorException(Exception exception) {
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
