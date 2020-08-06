package me.github.lparo.geolocation.api;

import me.github.lparo.geolocation.api.validation.IpValidator;
import me.github.lparo.geolocation.domain.City;
import me.github.lparo.geolocation.domain.Country;
import me.github.lparo.geolocation.domain.IpCityLocation;
import me.github.lparo.geolocation.domain.IpCountryLocation;
import me.github.lparo.geolocation.domain.State;
import me.github.lparo.geolocation.exception.InvalidIpException;
import me.github.lparo.geolocation.service.IpCityLocationService;
import me.github.lparo.geolocation.service.IpCountryLocationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IpLocationApiTest {
    private static final String VALID_IP = "217.138.219.147";
    private static final String INVALID_IP = "invalid";

    @InjectMocks
    private IpLocationApi ipLocationApi;

    @Mock
    private IpValidator ipValidator;

    @Mock
    private IpCityLocationService ipCityLocationService;

    @Mock
    private IpCountryLocationService ipCountryLocationService;

    @Test
    public void getCityLocation_whenIpIsInvalid_shouldThrowAnError() {
        doThrow(new InvalidIpException("invalid ip")).when(ipValidator).validateIp(INVALID_IP);

        assertThrows(InvalidIpException.class, () -> ipLocationApi.getCityLocation(INVALID_IP));

        verify(ipValidator, times(1)).validateIp(INVALID_IP);
        verifyNoMoreInteractions(ipValidator);

        verifyNoInteractions(ipCityLocationService);
        verifyNoInteractions(ipCountryLocationService);
    }

    @Test
    public void getCityLocation_whenIpIsNull_shouldGetTheCityLocationOfTheHostMachinePublicIp() {
        final IpCityLocation ipCityLocationDomain = createIpCityLocationDomain();

        when(ipCityLocationService.getCityLocationForHostIp()).thenReturn(ipCityLocationDomain);

        ipLocationApi.getCityLocation(null);

        verify(ipValidator, times(1)).validateIp(null);
        verify(ipCityLocationService, times(1)).getCityLocationForHostIp();

        verifyNoMoreInteractions(ipValidator);
        verifyNoMoreInteractions(ipCityLocationService);

        verifyNoInteractions(ipCountryLocationService);
    }

    @Test
    public void getCityLocation_whenIpIsEmpty_shouldGetTheCityLocationOfTheHostMachinePublicIp() {
        final IpCityLocation ipCityLocationDomain = createIpCityLocationDomain();

        when(ipCityLocationService.getCityLocationForHostIp()).thenReturn(ipCityLocationDomain);

        ipLocationApi.getCityLocation("");

        verify(ipValidator, times(1)).validateIp("");
        verify(ipCityLocationService, times(1)).getCityLocationForHostIp();

        verifyNoMoreInteractions(ipValidator);
        verifyNoMoreInteractions(ipCityLocationService);

        verifyNoInteractions(ipCountryLocationService);
    }

    @Test
    public void getCityLocation_whenIpIsPresent_shouldGetItsTheCityLocation() {
        final IpCityLocation ipCityLocationDomain = createIpCityLocationDomain();

        when(ipCityLocationService.getCityLocationForIp(VALID_IP)).thenReturn(ipCityLocationDomain);

        ipLocationApi.getCityLocation(VALID_IP);

        verify(ipValidator, times(1)).validateIp(VALID_IP);
        verify(ipCityLocationService, times(1)).getCityLocationForIp(VALID_IP);

        verifyNoMoreInteractions(ipValidator);
        verifyNoMoreInteractions(ipCityLocationService);

        verifyNoInteractions(ipCountryLocationService);
    }

    @Test
    public void getCountryLocation_whenIpIsInvalid_shouldThrowAnError() {
        doThrow(new InvalidIpException("invalid ip")).when(ipValidator).validateIp(INVALID_IP);

        assertThrows(InvalidIpException.class, () -> ipLocationApi.getCountryLocation(INVALID_IP));

        verify(ipValidator, times(1)).validateIp(INVALID_IP);
        verifyNoMoreInteractions(ipValidator);

        verifyNoInteractions(ipCityLocationService);
        verifyNoInteractions(ipCountryLocationService);
    }

    @Test
    public void getCountryLocation_whenIpIsNull_shouldGetTheCountryLocationOfTheHostMachinePublicIp() {
        final IpCountryLocation ipCountryLocationDomain = createIpCountryLocationDomain();

        when(ipCountryLocationService.getCountryLocationForHostIp()).thenReturn(ipCountryLocationDomain);

        ipLocationApi.getCountryLocation(null);

        verify(ipValidator, times(1)).validateIp(null);
        verify(ipCountryLocationService, times(1)).getCountryLocationForHostIp();

        verifyNoMoreInteractions(ipValidator);
        verifyNoMoreInteractions(ipCountryLocationService);

        verifyNoInteractions(ipCityLocationService);
    }

    @Test
    public void getCountryLocation_whenIpIsEmpty_shouldGetTheCityLocationOfTheHostMachinePublicIp() {
        final IpCountryLocation ipCountryLocationDomain = createIpCountryLocationDomain();

        when(ipCountryLocationService.getCountryLocationForHostIp()).thenReturn(ipCountryLocationDomain);

        ipLocationApi.getCountryLocation("");

        verify(ipValidator, times(1)).validateIp("");
        verify(ipCountryLocationService, times(1)).getCountryLocationForHostIp();

        verifyNoMoreInteractions(ipValidator);
        verifyNoMoreInteractions(ipCountryLocationService);

        verifyNoInteractions(ipCityLocationService);
    }

    @Test
    public void getCountryLocation_whenIpIsPresent_shouldGetItsTheCityLocation() {
        final IpCountryLocation ipCountryLocationDomain = createIpCountryLocationDomain();

        when(ipCountryLocationService.getCountryLocationForIp(VALID_IP)).thenReturn(ipCountryLocationDomain);

        ipLocationApi.getCountryLocation(VALID_IP);

        verify(ipValidator, times(1)).validateIp(VALID_IP);
        verify(ipCountryLocationService, times(1)).getCountryLocationForIp(VALID_IP);

        verifyNoMoreInteractions(ipValidator);
        verifyNoMoreInteractions(ipCountryLocationService);

        verifyNoInteractions(ipCityLocationService);
    }

    private IpCityLocation createIpCityLocationDomain() {
        return IpCityLocation.of(
                City.of(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt()),
                State.of(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt(), UUID.randomUUID().toString())
        );
    }

    private IpCountryLocation createIpCountryLocationDomain() {
        return IpCountryLocation.of(
                Country.of(
                        UUID.randomUUID().toString(),
                        ThreadLocalRandom.current().nextInt(),
                        ThreadLocalRandom.current().nextBoolean(),
                        UUID.randomUUID().toString()
                )
        );
    }
}