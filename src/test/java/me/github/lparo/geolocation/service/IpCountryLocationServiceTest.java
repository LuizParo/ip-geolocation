package me.github.lparo.geolocation.service;

import me.github.lparo.geolocation.repository.IpCountryLocationRepository;
import me.github.lparo.geolocation.domain.Country;
import me.github.lparo.geolocation.domain.IpCountryLocation;
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
class IpCountryLocationServiceTest {
    private static final String IP = "217.138.219.200";

    private IpCountryLocationService ipCountryLocationService;

    @Mock
    private IpCountryLocationRepository ipCountryLocationRepository;
    private final String hostMachinePublicIp = "217.138.219.147";

    @BeforeEach
    void setUp() {
        this.ipCountryLocationService = new IpCountryLocationService(hostMachinePublicIp, ipCountryLocationRepository);
    }

    @Test
    void getCountryLocationForHostIp_whenLocationIsFoundForHostIp_shouldReturnIt() {
        final IpCountryLocation expectedIpCountryLocation = createIpCountryLocationDomain();

        when(ipCountryLocationRepository.getCountryLocationForIp(hostMachinePublicIp)).thenReturn(Optional.of(expectedIpCountryLocation));

        final IpCountryLocation ipCountryLocation = ipCountryLocationService.getCountryLocationForHostIp();

        verify(ipCountryLocationRepository, times(1)).getCountryLocationForIp(hostMachinePublicIp);
        verifyNoMoreInteractions(ipCountryLocationRepository);

        assertThat(ipCountryLocation, notNullValue());
        assertThat(ipCountryLocation, is(expectedIpCountryLocation));
    }

    @Test
    void getCountryLocationForHostIp_whenLocationIsNotFoundForHostIp_shouldThrowError() {
        when(ipCountryLocationRepository.getCountryLocationForIp(hostMachinePublicIp)).thenReturn(Optional.empty());

        try {
            assertThrows(
                    LocationNotFoundException.class,
                    () -> ipCountryLocationService.getCountryLocationForHostIp(),
                    "unable to find country location for IP " + hostMachinePublicIp
            );
        } finally {
            verify(ipCountryLocationRepository, times(1)).getCountryLocationForIp(hostMachinePublicIp);
            verifyNoMoreInteractions(ipCountryLocationRepository);
        }
    }

    @Test
    void getCountryLocationForHostIp_whenLocationIsFoundForIp_shouldReturnIt() {
        final IpCountryLocation expectedIpCountryLocation = createIpCountryLocationDomain();

        when(ipCountryLocationRepository.getCountryLocationForIp(IP)).thenReturn(Optional.of(expectedIpCountryLocation));

        final IpCountryLocation ipCountryLocation = ipCountryLocationService.getCountryLocationForIp(IP);

        verify(ipCountryLocationRepository, times(1)).getCountryLocationForIp(IP);
        verifyNoMoreInteractions(ipCountryLocationRepository);

        assertThat(ipCountryLocation, notNullValue());
        assertThat(ipCountryLocation, is(expectedIpCountryLocation));
    }

    @Test
    void getCountryLocationForHostIp_whenLocationIsNotFoundForIp_shouldThrowError() {
        when(ipCountryLocationRepository.getCountryLocationForIp(IP)).thenReturn(Optional.empty());

        try {
            assertThrows(
                    LocationNotFoundException.class,
                    () -> ipCountryLocationService.getCountryLocationForIp(IP),
                    "unable to find country location for IP " + IP
            );
        } finally {
            verify(ipCountryLocationRepository, times(1)).getCountryLocationForIp(IP);
            verifyNoMoreInteractions(ipCountryLocationRepository);
        }
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