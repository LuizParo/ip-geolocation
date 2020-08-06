package me.github.lparo.geolocation.database.impl.geoip2;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.github.lparo.geolocation.database.IpCountryLocationRepository;
import me.github.lparo.geolocation.database.impl.transformer.IpCountryLocationTransformer;
import me.github.lparo.geolocation.domain.IpCountryLocation;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;

/**
 * Implementing class of {@link IpCountryLocationRepository} responsible for fetching the {@link IpCountryLocation} information
 * from the underlying GeoIP2 {@link DatabaseReader}.
 */
@Slf4j
@Component
@AllArgsConstructor
public class GeoIP2IpCountryLocationRepository implements IpCountryLocationRepository {
    private final DatabaseReader databaseReader;
    private final IpCountryLocationTransformer ipCountryLocationTransformer;

    /**
     * Searches the GeoIP2 datastore to find the country information of where the IP address is located. If the location is found,
     * an {@link IpCountryLocation} instance wrapped in an {@link Optional} is returned, otherwise, an {@link Optional#empty()}
     * is returned.
     *
     * @param ip the IP address to have the {@link IpCountryLocation} information fetched.
     *
     * @return the country information of where the IP address is located wrapped in an {@link Optional<IpCountryLocation>}.
     */
    public Optional<IpCountryLocation> getCountryLocationForIp(String ip) {
        try {
            return databaseReader.tryCountry(InetAddress.getByName(ip))
                                 .map(ipCountryLocationTransformer);
        } catch (IOException | GeoIp2Exception e) {
            log.error("unable to get country location for IP " + ip, e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
