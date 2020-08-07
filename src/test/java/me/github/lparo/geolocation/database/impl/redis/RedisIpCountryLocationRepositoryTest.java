package me.github.lparo.geolocation.database.impl.redis;

import me.github.lparo.geolocation.config.RedisConfigForTesting;
import me.github.lparo.geolocation.domain.Country;
import me.github.lparo.geolocation.domain.IpCountryLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
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
        RedisIpCountryLocationRepository.class
}, webEnvironment = WebEnvironment.NONE)
@DirtiesContext
@ActiveProfiles("integration-test")
@ExtendWith(SpringExtension.class)
class RedisIpCountryLocationRepositoryTest {
    private static final String CACHED_IP = "217.138.219.147";
    private static final String UNCACHED_IP = "127.0.0.1";

    private static final IpCountryLocation CACHED_IP_COUNTRY_LOCATION = createIpCountryLocation();
    private static final IpCountryLocation UNCACHED_IP_COUNTRY_LOCATION = createIpCountryLocation();

    @Autowired
    private RedisIpCountryLocationRepository repository;

    @Autowired
    private RedisTemplate<String, ?> redisTemplate;

    private HashOperations<String, String, IpCountryLocation> hashOperations;

    @PostConstruct
    public void init() {
        this.hashOperations = redisTemplate.opsForHash();
        this.hashOperations.put("COUNTRY", CACHED_IP, CACHED_IP_COUNTRY_LOCATION);
    }

    @Test
    void getCountryLocationForIp_whenCalledWithUncachedIp_shouldReturnOptionalEmpty() {
        final Optional<IpCountryLocation> ipCountryLocation = repository.getCountryLocationForIp(UNCACHED_IP);
        assertThat(ipCountryLocation, notNullValue());
        assertThat(ipCountryLocation.isPresent(), is(FALSE));
    }

    @Test
    void getCountryLocationForIp_whenCalledWithCachedIp_shouldReturnLocationWrappedInAnOptional() {
        final Optional<IpCountryLocation> ipCountryLocation = repository.getCountryLocationForIp(CACHED_IP);
        assertThat(ipCountryLocation, notNullValue());
        assertThat(ipCountryLocation.isPresent(), is(TRUE));
        assertThat(ipCountryLocation, is(Optional.of(CACHED_IP_COUNTRY_LOCATION)));
    }

    @Test
    public void addToCache_whenCalledWithLocation_shouldSaveItIntoTheCache() {
        final IpCountryLocation cachedIpCountryLocation = repository.addToCache(UNCACHED_IP, UNCACHED_IP_COUNTRY_LOCATION);
        assertThat(cachedIpCountryLocation, notNullValue());
        assertThat(cachedIpCountryLocation, is(UNCACHED_IP_COUNTRY_LOCATION));

        assertThat(hashOperations.hasKey("COUNTRY", UNCACHED_IP), is(TRUE));
    }

    private static IpCountryLocation createIpCountryLocation() {
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