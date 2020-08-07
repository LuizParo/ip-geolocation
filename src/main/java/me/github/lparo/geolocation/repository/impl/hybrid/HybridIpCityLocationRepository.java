package me.github.lparo.geolocation.repository.impl.hybrid;

import lombok.AllArgsConstructor;
import me.github.lparo.geolocation.domain.IpCityLocation;
import me.github.lparo.geolocation.repository.IpCityLocationRepository;
import me.github.lparo.geolocation.repository.impl.geoip2.GeoIP2IpCityLocationRepository;
import me.github.lparo.geolocation.repository.impl.redis.RedisIpCityLocationRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Implementing class of {@link IpCityLocationRepository} responsible for fetching the {@link IpCityLocation} information
 * from Redis cache, or the GeoIP2 repository if it's missing there.
 */
@Primary
@Repository
@AllArgsConstructor
public class HybridIpCityLocationRepository implements IpCityLocationRepository {
    private final GeoIP2IpCityLocationRepository geoIP2IpCityLocationRepository;
    private final RedisIpCityLocationRepository redisIpCityLocationRepository;

    /**
     * Tries to fetch the {@link IpCityLocation} from the Redis cache and return it wrapped in an {@link Optional}. It
     * uses the IP address as a locator for the cached information. If the location for the IP is missing from the cache,
     * then it's gonna try to retrieve it from the GeoIP2 repository, and if it is in there, it's then added to the cache
     * for posterior calls. In case the location is absent on both Redis and GeoIP2 repositories, then an {@link Optional#empty()}
     * is returned instead.
     *
     * @param ip the IP address to have the {@link IpCityLocation} information fetched.
     *
     * @return the found {@link IpCityLocation} wrapped in an {@link Optional}, or {@link Optional#empty()} if not found.
     */
    @Override
    public Optional<IpCityLocation> getCityLocationForIp(String ip) {
        return redisIpCityLocationRepository.getCityLocationForIp(ip)
                .or(() ->
                        geoIP2IpCityLocationRepository.getCityLocationForIp(ip)
                                .map(ipCityLocation -> redisIpCityLocationRepository.addToCache(ip, ipCityLocation))
                );
    }
}
