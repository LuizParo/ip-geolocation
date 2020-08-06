package me.github.lparo.geolocation.service;

import me.github.lparo.geolocation.database.IpCountryLocationRepository;
import me.github.lparo.geolocation.domain.IpCountryLocation;
import me.github.lparo.geolocation.exception.LocationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service responsible for concentrating the logic for the {@link IpCountryLocation} domain.
 */
@Service
public class IpCountryLocationService {

    /**
     * the public IP of the service's host machine.
     */
    private final String hostMachinePublicIp;
    private final IpCountryLocationRepository ipCountryLocationRepository;

    @Autowired
    public IpCountryLocationService(@Qualifier("hostMachinePublicIp") String hostMachinePublicIp,
                                    IpCountryLocationRepository ipCountryLocationRepository) {
        this.hostMachinePublicIp = hostMachinePublicIp;
        this.ipCountryLocationRepository = ipCountryLocationRepository;
    }

    /**
     * Gets the {@link IpCountryLocation} containing the country information for the public IP address from the machine
     * the service is hosted on. If IP address does not resolve to any geolocation, then an {@link LocationNotFoundException}
     * is thrown.
     *
     * @return the {@link IpCountryLocation} containing the country information of the public IP address of the service's host machine.
     *
     * @throws LocationNotFoundException if the IP address does not resolve to any geolocation.
     */
    public IpCountryLocation getCountryLocationForHostIp() {
        return getCountryLocationForIp(hostMachinePublicIp);
    }

    /**
     * Gets the {@link IpCountryLocation} containing the country information for a given IP address. If IP address does
     * not resolve to any geolocation, then an {@link LocationNotFoundException} is thrown.
     *
     * @return the {@link IpCountryLocation} containing the country information of given IP address.
     *
     * @throws LocationNotFoundException if the IP address does not resolve to any geolocation.
     */
    public IpCountryLocation getCountryLocationForIp(String ip) {
        return ipCountryLocationRepository.getCountryLocationForIp(ip)
                                  .orElseThrow(() -> new LocationNotFoundException("unable to find country location for IP " + ip));
    }
}
