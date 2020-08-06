package me.github.lparo.geolocation.controller.dto;

import lombok.Value;

/**
 * DTO representation of any {@link Throwable} that was thrown during a request and needs to be sent within a response body.
 */
@Value
public class ExceptionBody {
    String message;

    /**
     * Creates a DTO representation of a {@link Throwable}.
     *
     * @param throwable the throwable to be converted to DTO.
     *
     * @return an {@link ExceptionBody} representing the original {@link Throwable} that was thrown during the request.
     */
    public static ExceptionBody fromThrowable(Throwable throwable) {
        return new ExceptionBody(throwable.getMessage());
    }
}
