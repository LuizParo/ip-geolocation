package me.github.lparo.geolocation.controller.exception;

import lombok.extern.slf4j.Slf4j;
import me.github.lparo.geolocation.controller.dto.ExceptionBody;
import me.github.lparo.geolocation.exception.InvalidIpException;
import me.github.lparo.geolocation.exception.LocationNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Controller advice to handle eventual exceptions that happen during the request processing.
 *
 * @see me.github.lparo.geolocation.controller.IpLocationController
 */
@Slf4j
@ControllerAdvice
public class IpLocationControllerAdvice {

    /**
     * Handles occurrences of {@link InvalidIpException}. It maps the exception with HTTP status of 400 - Bad Request.
     *
     * @param invalidIpException the thrown {@link InvalidIpException}
     *
     * @return a {@link ResponseEntity<ExceptionBody>} with HTTP status of 400 - Bad Request
     */
    @ExceptionHandler(InvalidIpException.class)
    public ResponseEntity<ExceptionBody> handleInvalidIpException(InvalidIpException invalidIpException) {
        log.error("request contains invalid IP: " + invalidIpException.getMessage(), invalidIpException);

        return ResponseEntity
                .badRequest()
                .body(ExceptionBody.fromThrowable(invalidIpException));
    }

    /**
     * Handles occurrences of {@link LocationNotFoundException}. It maps the exception with HTTP status of 404 - Not Found.
     *
     * @param locationNotFoundException the thrown {@link LocationNotFoundException}
     *
     * @return a {@link ResponseEntity<ExceptionBody>} with HTTP status of 404 - Not Found
     */
    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<ExceptionBody> handleLocationNotFoundException(LocationNotFoundException locationNotFoundException) {
        log.error("unable to find location for IP address", locationNotFoundException);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionBody.fromThrowable(locationNotFoundException));
    }

    /**
     * Handles occurrences of {@link Exception}. It maps the exception with HTTP status of 500 - Internal Server Error.
     *
     * @param exception the thrown {@link Exception}
     *
     * @return a {@link ResponseEntity<ExceptionBody>} with HTTP status of 500 - Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionBody> handleException(Exception exception) {
        log.error("an unexpected error happened while processing request: " + exception.getMessage(), exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionBody.fromThrowable(exception));
    }
}
