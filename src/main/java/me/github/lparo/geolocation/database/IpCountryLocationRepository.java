package me.github.lparo.geolocation.database;

import me.github.lparo.geolocation.domain.IpCountryLocation;

import java.util.Optional;

/**
 * Interface responsible for providing a contract for {@link IpCountryLocation} retrieval. The implementing class should
 * deal with the specifics of how to retrieve this information.
 */
public interface IpCountryLocationRepository {

    /**
     * Fetches an {@link IpCountryLocation} from the underlying data store and returns it wrapped into an {@link Optional}.
     *
     * @param ip the IP address to have the {@link IpCountryLocation} information fetched.
     *
     * @return the country information of where the IP address is located wrapped in an {@link Optional<IpCountryLocation>}.
     */
    Optional<IpCountryLocation> getCountryLocationForIp(String ip);
}
