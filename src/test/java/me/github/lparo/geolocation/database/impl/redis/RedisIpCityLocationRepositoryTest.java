package me.github.lparo.geolocation.database.impl.redis;

import me.github.lparo.geolocation.config.RedisConfigForTesting;
import me.github.lparo.geolocation.domain.City;
import me.github.lparo.geolocation.domain.IpCityLocation;
import me.github.lparo.geolocation.domain.State;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(classes = {
        RedisConfigForTesting.class,
        RedisIpCityLocationRepository.class
}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext
@ActiveProfiles("integration-test")
@ExtendWith(SpringExtension.class)
class RedisIpCityLocationRepositoryTest {
    private static final String CACHED_IP = "217.138.219.147";
    private static final String UNCACHED_IP = "127.0.0.1";

    private static final IpCityLocation CACHED_IP_CITY_LOCATION = createIpCityLocation();
    private static final IpCityLocation UNCACHED_IP_CITY_LOCATION = createIpCityLocation();

    @Autowired
    private RedisIpCityLocationRepository repository;

    @Autowired
    private RedisTemplate<String, ?> redisTemplate;

    private HashOperations<String, String, IpCityLocation> hashOperations;

    @PostConstruct
    public void init() {
        this.hashOperations = redisTemplate.opsForHash();
        this.hashOperations.put("CITY", CACHED_IP, CACHED_IP_CITY_LOCATION);
    }

    @Test
    void getCityLocationForIp_whenCalledWithUncachedIp_shouldReturnOptionalEmpty() {
        final Optional<IpCityLocation> ipCityLocation = repository.getCityLocationForIp(UNCACHED_IP);
        assertThat(ipCityLocation, notNullValue());
        assertThat(ipCityLocation.isPresent(), is(FALSE));
    }

    @Test
    void getCityLocationForIp_whenCalledWithCachedIp_shouldReturnLocationWrappedInAnOptional() {
        final Optional<IpCityLocation> IpCityLocation = repository.getCityLocationForIp(CACHED_IP);
        assertThat(IpCityLocation, notNullValue());
        assertThat(IpCityLocation.isPresent(), is(TRUE));
        assertThat(IpCityLocation, is(Optional.of(CACHED_IP_CITY_LOCATION)));
    }

    @Test
    public void addToCache_whenCalledWithLocation_shouldSaveItIntoTheCache() {
        final IpCityLocation cachedIpCityLocation = repository.addToCache(UNCACHED_IP, UNCACHED_IP_CITY_LOCATION);
        assertThat(cachedIpCityLocation, notNullValue());
        assertThat(cachedIpCityLocation, is(UNCACHED_IP_CITY_LOCATION));

        assertThat(hashOperations.hasKey("CITY", UNCACHED_IP), is(TRUE));
    }

    private static IpCityLocation createIpCityLocation() {
        return IpCityLocation.of(
                City.of(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt()),
                State.of(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt(), UUID.randomUUID().toString())
        );
    }
}