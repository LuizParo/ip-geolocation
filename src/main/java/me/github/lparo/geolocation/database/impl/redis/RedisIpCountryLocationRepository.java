package me.github.lparo.geolocation.database.impl.redis;

import me.github.lparo.geolocation.database.IpCountryLocationRepository;
import me.github.lparo.geolocation.domain.IpCountryLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

/**
 * Implementing class of {@link IpCountryLocationRepository} responsible for fetching the {@link IpCountryLocation} information
 * from the underlying Redis server, which is being used as a cache.
 */
@Repository("redisIpCountryLocationRepository")
public class RedisIpCountryLocationRepository implements IpCountryLocationRepository {
    private static final String REPOSITORY_TYPE = "COUNTRY";

    private final HashOperations<String, String, IpCountryLocation> hashOperations;

    @Autowired
    public RedisIpCountryLocationRepository(RedisTemplate<String, ? extends Serializable> redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
    }

    /**
     * Tries to fetch the {@link IpCountryLocation} from the Redis cache and returns it as an {@link Optional}. If the
     * information is missing from Redis, then an {@link Optional#empty()} is returned instead. It uses the IP address
     * as a locator key in the cache.
     *
     * @param ip the IP address to have the {@link IpCountryLocation} information fetched.
     *
     * @return the {@link IpCountryLocation} information of where the IP address is located wrapped in an {@link Optional}.
     */
    @Override
    public Optional<IpCountryLocation> getCountryLocationForIp(String ip) {
        return Optional.ofNullable(hashOperations.get(REPOSITORY_TYPE, ip));
    }

    /**
     * Adds a single {@link IpCountryLocation} in the cache, associating it with its origin IP address (as the locator key).
     *
     * @param ip the IP address to be used as a locator key for the incoming {@link IpCountryLocation}.
     * @param ipCountryLocation the {@link IpCountryLocation} to be persisted in the Redis cache.
     *
     * @return the {@link IpCountryLocation} that was just saved into the Redis cache.
     */
    public IpCountryLocation addToCache(String ip, IpCountryLocation ipCountryLocation) {
        hashOperations.put(REPOSITORY_TYPE, ip, ipCountryLocation);
        return ipCountryLocation;
    }
}
