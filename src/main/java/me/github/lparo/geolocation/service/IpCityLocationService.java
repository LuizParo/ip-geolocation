package me.github.lparo.geolocation.service;

import me.github.lparo.geolocation.database.IpCityLocationRepository;
import me.github.lparo.geolocation.domain.IpCityLocation;
import me.github.lparo.geolocation.exception.LocationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service responsible for concentrating the logic for the {@link IpCityLocation} domain.
 */
@Service
public class IpCityLocationService {

    /**
     * the public IP of the service's host machine.
     */
    private final String hostMachinePublicIp;
    private final IpCityLocationRepository ipCityLocationRepository;

    @Autowired
    public IpCityLocationService(@Qualifier("hostMachinePublicIp") String hostMachinePublicIp,
                                 IpCityLocationRepository ipCityLocationRepository) {
        this.hostMachinePublicIp = hostMachinePublicIp;
        this.ipCityLocationRepository = ipCityLocationRepository;
    }

    /**
     * Gets the {@link IpCityLocation} containing the city/state information for the public IP address from the machine
     * the service is hosted on. If IP address does not resolve to any geolocation, then an {@link LocationNotFoundException}
     * is thrown.
     *
     * @return the {@link IpCityLocation} containing the city/state information of the public IP address of the service's host machine.
     *
     * @throws LocationNotFoundException if the IP address does not resolve to any geolocation.
     */
    public IpCityLocation getCityLocationForHostIp() {
        return getCityLocationForIp(hostMachinePublicIp);
    }

    /**
     * Gets the {@link IpCityLocation} containing the city/state information for a given IP address. If IP address does
     * not resolve to any geolocation, then an {@link LocationNotFoundException} is thrown.
     *
     * @return the {@link IpCityLocation} containing the city/state information of given IP address.
     *
     * @throws LocationNotFoundException if the IP address does not resolve to any geolocation.
     */
    public IpCityLocation getCityLocationForIp(String ip) {
        return ipCityLocationRepository.getCityLocationForIp(ip)
                                  .orElseThrow(() -> new LocationNotFoundException("unable to find city location for IP " + ip));
    }
}
