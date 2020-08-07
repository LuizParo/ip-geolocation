package me.github.lparo.geolocation.repository.impl.hybrid;

import me.github.lparo.geolocation.repository.impl.geoip2.GeoIP2IpCityLocationRepository;
import me.github.lparo.geolocation.repository.impl.redis.RedisIpCityLocationRepository;
import me.github.lparo.geolocation.domain.City;
import me.github.lparo.geolocation.domain.IpCityLocation;
import me.github.lparo.geolocation.domain.State;
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
class HybridIpCityLocationRepositoryTest {
    private static final String IP = "217.138.219.200";

    @InjectMocks
    private HybridIpCityLocationRepository hybridIpCityLocationRepository;

    @Mock
    private GeoIP2IpCityLocationRepository geoIP2IpCityLocationRepository;

    @Mock
    private RedisIpCityLocationRepository redisIpCityLocationRepository;

    @Test
    void getCityLocationForIp_whenTheLocationIsFoundInTheCache_shouldReturnItWrappedInAnOptional() {
        final IpCityLocation expectedIpCityLocation = createIpCityLocation();

        when(redisIpCityLocationRepository.getCityLocationForIp(IP)).thenReturn(Optional.of(expectedIpCityLocation));

        final Optional<IpCityLocation> ipCityLocation = hybridIpCityLocationRepository.getCityLocationForIp(IP);

        verify(redisIpCityLocationRepository, times(1)).getCityLocationForIp(IP);

        verifyNoMoreInteractions(redisIpCityLocationRepository);
        verifyNoInteractions(geoIP2IpCityLocationRepository);

        assertThat(ipCityLocation, notNullValue());
        assertThat(ipCityLocation.isPresent(), is(TRUE));
        assertThat(ipCityLocation, is(Optional.of(expectedIpCityLocation)));
    }

    @Test
    void getCityLocationForIp_whenTheLocationIsNotFoundInTheCache_andIsFoundInTheGeoIP2Repository_shouldPersistTheLocationInTheCache_andReturnItWrappedInAnOptional() {
        final IpCityLocation expectedIpCityLocation = createIpCityLocation();

        when(redisIpCityLocationRepository.getCityLocationForIp(IP)).thenReturn(Optional.empty());
        when(geoIP2IpCityLocationRepository.getCityLocationForIp(IP)).thenReturn(Optional.of(expectedIpCityLocation));
        when(redisIpCityLocationRepository.addToCache(IP, expectedIpCityLocation)).thenReturn(expectedIpCityLocation);

        final Optional<IpCityLocation> ipCityLocation = hybridIpCityLocationRepository.getCityLocationForIp(IP);

        verify(redisIpCityLocationRepository, times(1)).getCityLocationForIp(IP);
        verify(redisIpCityLocationRepository, times(1)).addToCache(IP, expectedIpCityLocation);
        verify(geoIP2IpCityLocationRepository, times(1)).getCityLocationForIp(IP);

        verifyNoMoreInteractions(redisIpCityLocationRepository);
        verifyNoMoreInteractions(geoIP2IpCityLocationRepository);

        assertThat(ipCityLocation, notNullValue());
        assertThat(ipCityLocation.isPresent(), is(TRUE));
        assertThat(ipCityLocation, is(Optional.of(expectedIpCityLocation)));
    }

    @Test
    void getCityLocationForIp_whenTheLocationIsNotFoundInTheCache_andIsNotFoundInTheGeoIP2RepositoryEither_shouldReturnOptionalEmpty() {
        when(redisIpCityLocationRepository.getCityLocationForIp(IP)).thenReturn(Optional.empty());
        when(geoIP2IpCityLocationRepository.getCityLocationForIp(IP)).thenReturn(Optional.empty());

        final Optional<IpCityLocation> ipCityLocation = hybridIpCityLocationRepository.getCityLocationForIp(IP);

        verify(redisIpCityLocationRepository, times(1)).getCityLocationForIp(IP);
        verify(geoIP2IpCityLocationRepository, times(1)).getCityLocationForIp(IP);

        verifyNoMoreInteractions(redisIpCityLocationRepository);
        verifyNoMoreInteractions(geoIP2IpCityLocationRepository);

        assertThat(ipCityLocation, notNullValue());
        assertThat(ipCityLocation.isPresent(), is(FALSE));
    }

    private IpCityLocation createIpCityLocation() {
        return IpCityLocation.of(
                City.of(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt()),
                State.of(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt(), UUID.randomUUID().toString())
        );
    }
}