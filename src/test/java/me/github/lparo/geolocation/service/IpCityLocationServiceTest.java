package me.github.lparo.geolocation.service;

import me.github.lparo.geolocation.repository.IpCityLocationRepository;
import me.github.lparo.geolocation.domain.City;
import me.github.lparo.geolocation.domain.IpCityLocation;
import me.github.lparo.geolocation.domain.State;
import me.github.lparo.geolocation.exception.LocationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IpCityLocationServiceTest {
    private static final String IP = "217.138.219.200";

    private IpCityLocationService ipCityLocationService;

    @Mock
    private IpCityLocationRepository ipCityLocationRepository;
    private final String hostMachinePublicIp = "217.138.219.147";

    @BeforeEach
    void setUp() {
        this.ipCityLocationService = new IpCityLocationService(hostMachinePublicIp, ipCityLocationRepository);
    }

    @Test
    void getCityLocationForHostIp_whenLocationIsFoundForHostIp_shouldReturnIt() {
        final IpCityLocation expectedIpCityLocation = createIpCityLocation();

        when(ipCityLocationRepository.getCityLocationForIp(hostMachinePublicIp)).thenReturn(Optional.of(expectedIpCityLocation));

        final IpCityLocation ipCityLocation = ipCityLocationService.getCityLocationForHostIp();

        verify(ipCityLocationRepository, times(1)).getCityLocationForIp(hostMachinePublicIp);
        verifyNoMoreInteractions(ipCityLocationRepository);

        assertThat(ipCityLocation, notNullValue());
        assertThat(ipCityLocation, is(expectedIpCityLocation));
    }

    @Test
    void getCityLocationForHostIp_whenLocationIsNotFoundForHostIp_shouldThrowError() {
        when(ipCityLocationRepository.getCityLocationForIp(hostMachinePublicIp)).thenReturn(Optional.empty());

        try {
            assertThrows(
                    LocationNotFoundException.class,
                    () -> ipCityLocationService.getCityLocationForHostIp(),
                    "unable to find city location for IP " + hostMachinePublicIp
            );
        } finally {
            verify(ipCityLocationRepository, times(1)).getCityLocationForIp(hostMachinePublicIp);
            verifyNoMoreInteractions(ipCityLocationRepository);
        }
    }

    @Test
    void getCityLocationForIp_whenLocationIsFoundForIp_shouldReturnIt() {
        final IpCityLocation expectedIpCityLocation = createIpCityLocation();

        when(ipCityLocationRepository.getCityLocationForIp(IP)).thenReturn(Optional.of(expectedIpCityLocation));

        final IpCityLocation ipCityLocation = ipCityLocationService.getCityLocationForIp(IP);

        verify(ipCityLocationRepository, times(1)).getCityLocationForIp(IP);
        verifyNoMoreInteractions(ipCityLocationRepository);

        assertThat(ipCityLocation, notNullValue());
        assertThat(ipCityLocation, is(expectedIpCityLocation));
    }

    @Test
    void getCityLocationForIp_whenLocationIsNotFoundForIp_shouldThrowError() {
        when(ipCityLocationRepository.getCityLocationForIp(IP)).thenReturn(Optional.empty());

        try {
            assertThrows(
                    LocationNotFoundException.class,
                    () -> ipCityLocationService.getCityLocationForIp(IP),
                    "unable to find city location for IP " + hostMachinePublicIp
            );
        } finally {
            verify(ipCityLocationRepository, times(1)).getCityLocationForIp(IP);
            verifyNoMoreInteractions(ipCityLocationRepository);
        }
    }

    private IpCityLocation createIpCityLocation() {
        return IpCityLocation.of(
                City.of(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt()),
                State.of(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt(), UUID.randomUUID().toString())
        );
    }
}