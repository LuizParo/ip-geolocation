package me.github.lparo.geolocation.controller.dto;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class ExceptionBodyTest {
    private static final String MESSAGE = "any message";
    private static final Throwable THROWABLE = new Throwable(MESSAGE);

    @Test
    void newExceptionBody_whenCalledWithParameters_shouldAssignThemToTheRightField() {
        final ExceptionBody exceptionBody = new ExceptionBody(MESSAGE);
        assertThat(exceptionBody.getMessage(), is(MESSAGE));
    }

    @Test
    void fromThrowable_whenCalledWithThrowable_shouldCreateADtoRepresentationOfIt() {
        final ExceptionBody exceptionBody = ExceptionBody.fromThrowable(THROWABLE);

        assertThat(exceptionBody, notNullValue());
        assertThat(exceptionBody.getMessage(), is(THROWABLE.getMessage()));
    }
}