package me.github.lparo.geolocation.repository;

import me.github.lparo.geolocation.domain.IpCityLocation;

import java.util.Optional;

/**
 * Interface responsible for providing a contract for {@link IpCityLocation} retrieval. The implementing class should
 * deal with the specifics of how to retrieve this information.
 */
public interface IpCityLocationRepository {

    /**
     * Fetches an {@link IpCityLocation} from the underlying data store and returns it wrapped into an {@link Optional}.
     *
     * @param ip the IP address to have the {@link IpCityLocation} information fetched.
     *
     * @return the country information of where the IP address is located wrapped in an {@link Optional<IpCityLocation>}.
     */
    Optional<IpCityLocation> getCityLocationForIp(String ip);
}
