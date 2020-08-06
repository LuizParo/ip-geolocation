package me.github.lparo.geolocation.api.validation;

import me.github.lparo.geolocation.exception.InvalidIpException;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IpValidatorTest {

    @InjectMocks
    private IpValidator ipValidator;

    @Mock
    private InetAddressValidator validator;

    @Test
    void validateIp_whenCalledWithNullIp_shouldNotThrowAnyError() {
        ipValidator.validateIp(null);

        verifyNoInteractions(validator);
    }

    @Test
    void validateIp_whenCalledWithEmptyIp_shouldNotThrowAnyError() {
        ipValidator.validateIp("");

        verifyNoInteractions(validator);
    }

    @Test
    void validateIp_whenCalledWithValidIp_shouldNotThrowAnyError() {
        final String ip = "127.0.0.1";

        when(validator.isValidInet4Address(ip)).thenReturn(TRUE);

        ipValidator.validateIp(ip);

        verifyNoMoreInteractions(validator);
    }

    @Test
    void validateIp_whenCalledWithInvalidIp_shouldThrowAnError() {
        final String ip = "invalid";

        when(validator.isValidInet4Address(ip)).thenReturn(FALSE);

        assertThrows(
                InvalidIpException.class,
                () -> ipValidator.validateIp(ip),
                "invalid IP format: " + ip
        );

        verifyNoMoreInteractions(validator);
    }
}