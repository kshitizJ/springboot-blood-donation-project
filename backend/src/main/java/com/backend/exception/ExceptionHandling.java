package com.backend.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.util.Objects;

import javax.persistence.NoResultException;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.backend.domain.HttpResponse;
import com.backend.exception.domain.EmailExistException;
import com.backend.exception.domain.EmailNotFoundException;
import com.backend.exception.domain.EmailValidationException;
import com.backend.exception.domain.PasswordDidNotMatchException;
import com.backend.exception.domain.ReceiverAndDonorException;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ExceptionHandling implements ErrorController {
    private static final String ACCOUNT_LOCKED = "Your account has been locked. Please contact administration.";
    private static final String METHOD_IS_NOT_ALLOWED = "This request is not allowed on this endpoint. Please send a '%s' request.";
    private static final String INTERNAL_SERVER_ERROR_MSG = "An error occured while processing the request.";
    private static final String INCORRECT_CREDENTIALS = "Username / password incorrect. Please try again.";
    private static final String ACCOUNT_DISABLED = "Your account has been disabled. If this is an error, please contact administration.";
    private static final String NOT_ENOUGH_PERMISSION = "You do not have enough permission.";
    public static final String ERROR_PATH = "/error";

    @RequestMapping(ERROR_PATH)
    public ResponseEntity<HttpResponse> notFound404() {

        return createHttpResponse(NOT_FOUND, "There is no mapping for this url.");

    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledException() {

        log.error(ACCOUNT_DISABLED);
        return createHttpResponse(BAD_REQUEST, ACCOUNT_DISABLED);

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException() {

        log.error(INCORRECT_CREDENTIALS);
        return createHttpResponse(BAD_REQUEST, INCORRECT_CREDENTIALS);

    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException() {

        log.error(NOT_ENOUGH_PERMISSION);
        return createHttpResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);

    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedException() {

        log.error(ACCOUNT_LOCKED);
        return createHttpResponse(UNAUTHORIZED, ACCOUNT_LOCKED);

    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException exception) {

        log.error(exception.getMessage());
        return createHttpResponse(UNAUTHORIZED, exception.getMessage());

    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<HttpResponse> emailExistException(EmailExistException exception) {

        log.error(exception.getMessage());
        return createHttpResponse(BAD_REQUEST, exception.getMessage());

    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException exception) {

        log.error(exception.getMessage());
        return createHttpResponse(BAD_REQUEST, exception.getMessage());

    }

    @ExceptionHandler(EmailValidationException.class)
    public ResponseEntity<HttpResponse> emailValidationException(EmailValidationException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(ReceiverAndDonorException.class)
    public ResponseEntity<HttpResponse> recieverAndDonorException(ReceiverAndDonorException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(PasswordDidNotMatchException.class)
    public ResponseEntity<HttpResponse> passwordDidNotMatch(PasswordDidNotMatchException exception) {

        log.error(exception.getMessage());
        return createHttpResponse(BAD_REQUEST, exception.getMessage());

    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {

        // Getting the supported method from the exception and saving it in HttpMethod's
        // instance.
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();

        log.error(String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
        return createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {

        log.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);

    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> notFoundException(NoResultException exception) {

        log.error(exception.getMessage());
        return createHttpResponse(NOT_FOUND, exception.getMessage());

    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {

        HttpResponse httpResponse = new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase(),
                message);

        return new ResponseEntity<>(httpResponse, httpStatus);

    }

}
