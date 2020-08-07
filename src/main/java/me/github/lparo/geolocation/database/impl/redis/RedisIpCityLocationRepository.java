package me.github.lparo.geolocation.database.impl.redis;

import me.github.lparo.geolocation.database.IpCityLocationRepository;
import me.github.lparo.geolocation.domain.IpCityLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

/**
 * Implementing class of {@link IpCityLocationRepository} responsible for fetching the {@link IpCityLocation} information
 * from the underlying Redis server, which is being used as a cache.
 */
@Repository("redisIpCityLocationRepository")
public class RedisIpCityLocationRepository implements IpCityLocationRepository {
    private static final String REPOSITORY_TYPE = "CITY";

    private final HashOperations<String, String, IpCityLocation> hashOperations;

    @Autowired
    public RedisIpCityLocationRepository(RedisTemplate<String, ? extends Serializable> redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
    }

    /**
     * Tries to fetch the {@link IpCityLocation} from the Redis cache and returns it as an {@link Optional}. If the
     * information is missing from Redis, then an {@link Optional#empty()} is returned instead. It uses the IP address
     * as a locator key in the cache.
     *
     * @param ip the IP address to have the {@link IpCityLocation} information fetched.
     *
     * @return the {@link IpCityLocation} information of where the IP address is located wrapped in an {@link Optional}.
     */
    @Override
    public Optional<IpCityLocation> getCityLocationForIp(String ip) {
        return Optional.ofNullable(hashOperations.get(REPOSITORY_TYPE, ip));
    }

    /**
     * Adds a single {@link IpCityLocation} in the cache, associating it with its origin IP address (as the locator key).
     *
     * @param ip the IP address to be used as a locator key for the incoming {@link IpCityLocation}.
     * @param ipCityLocation the {@link IpCityLocation} to be persisted in the Redis cache.
     *
     * @return the {@link IpCityLocation} that was just saved into the Redis cache.
     */
    public IpCityLocation addToCache(String ip, IpCityLocation ipCityLocation) {
        hashOperations.put(REPOSITORY_TYPE, ip, ipCityLocation);
        return ipCityLocation;
    }
}
