package me.github.lparo.geolocation.database.impl.hybrid;

import me.github.lparo.geolocation.database.impl.geoip2.GeoIP2IpCountryLocationRepository;
import me.github.lparo.geolocation.database.impl.redis.RedisIpCountryLocationRepository;
import me.github.lparo.geolocation.domain.Country;
import me.github.lparo.geolocation.domain.IpCountryLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HybridIpCountryLocationRepositoryTest {
    private static final String IP = "217.138.219.200";

    @InjectMocks
    private HybridIpCountryLocationRepository hybridIpCountryLocationRepository;

    @Mock
    private GeoIP2IpCountryLocationRepository geoIP2IpCountryLocationRepository;

    @Mock
    private RedisIpCountryLocationRepository redisIpCountryLocationRepository;

    @Test
    void getCountryLocationForIp_whenTheLocationIsFoundInTheCache_shouldReturnItWrappedInAnOptional() {
        final IpCountryLocation expectedIpCountryLocation = createIpCountryLocation();

        when(redisIpCountryLocationRepository.getCountryLocationForIp(IP)).thenReturn(Optional.of(expectedIpCountryLocation));

        final Optional<IpCountryLocation> ipCountryLocation = hybridIpCountryLocationRepository.getCountryLocationForIp(IP);

        verify(redisIpCountryLocationRepository, times(1)).getCountryLocationForIp(IP);

        verifyNoMoreInteractions(redisIpCountryLocationRepository);
        verifyNoInteractions(geoIP2IpCountryLocationRepository);

        assertThat(ipCountryLocation, notNullValue());
        assertThat(ipCountryLocation.isPresent(), is(TRUE));
        assertThat(ipCountryLocation, is(Optional.of(expectedIpCountryLocation)));
    }

    @Test
    void getCountryLocationForIp_whenTheLocationIsNotFoundInTheCache_andIsFoundInTheGeoIP2Repository_shouldPersistTheLocationInTheCache_andReturnItWrappedInAnOptional() {
        final IpCountryLocation expectedIpCountryLocation = createIpCountryLocation();

        when(redisIpCountryLocationRepository.getCountryLocationForIp(IP)).thenReturn(Optional.empty());
        when(geoIP2IpCountryLocationRepository.getCountryLocationForIp(IP)).thenReturn(Optional.of(expectedIpCountryLocation));
        when(redisIpCountryLocationRepository.addToCache(IP, expectedIpCountryLocation)).thenReturn(expectedIpCountryLocation);

        final Optional<IpCountryLocation> ipCountryLocation = hybridIpCountryLocationRepository.getCountryLocationForIp(IP);

        verify(redisIpCountryLocationRepository, times(1)).getCountryLocationForIp(IP);
        verify(redisIpCountryLocationRepository, times(1)).addToCache(IP, expectedIpCountryLocation);
        verify(geoIP2IpCountryLocationRepository, times(1)).getCountryLocationForIp(IP);

        verifyNoMoreInteractions(redisIpCountryLocationRepository);
        verifyNoMoreInteractions(geoIP2IpCountryLocationRepository);

        assertThat(ipCountryLocation, notNullValue());
        assertThat(ipCountryLocation.isPresent(), is(TRUE));
        assertThat(ipCountryLocation, is(Optional.of(expectedIpCountryLocation)));
    }

    @Test
    void getCountryLocationForIp_whenTheLocationIsNotFoundInTheCache_andIsNotFoundInTheGeoIP2RepositoryEither_shouldReturnOptionalEmpty() {
        when(redisIpCountryLocationRepository.getCountryLocationForIp(IP)).thenReturn(Optional.empty());
        when(geoIP2IpCountryLocationRepository.getCountryLocationForIp(IP)).thenReturn(Optional.empty());

        final Optional<IpCountryLocation> ipCountryLocation = hybridIpCountryLocationRepository.getCountryLocationForIp(IP);

        verify(redisIpCountryLocationRepository, times(1)).getCountryLocationForIp(IP);
        verify(geoIP2IpCountryLocationRepository, times(1)).getCountryLocationForIp(IP);

        verifyNoMoreInteractions(redisIpCountryLocationRepository);
        verifyNoMoreInteractions(geoIP2IpCountryLocationRepository);

        assertThat(ipCountryLocation, notNullValue());
        assertThat(ipCountryLocation.isPresent(), is(FALSE));
    }

    private IpCountryLocation createIpCountryLocation() {
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