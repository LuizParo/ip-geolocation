package me.github.lparo.geolocation.api;

import lombok.AllArgsConstructor;
import me.github.lparo.geolocation.api.validation.IpValidator;
import me.github.lparo.geolocation.controller.dto.IpCityLocation;
import me.github.lparo.geolocation.controller.dto.IpCountryLocation;
import me.github.lparo.geolocation.service.IpCityLocationService;
import me.github.lparo.geolocation.service.IpCountryLocationService;
import org.springframework.stereotype.Component;

/**
 * API class responsible for providing a facade for the interaction between the external (DTOs) and domain layers of the
 * application. It should contain as minimal logic as possible, mostly forwarding calls to methods of other classes and services.
 */
@Component
@AllArgsConstructor
public class IpLocationApi {
    private final IpValidator ipValidator;
    private final IpCityLocationService ipCityLocationService;
    private final IpCountryLocationService ipCountryLocationService;

    /**
     * Gets the city/state information of the given IP address. If the IP is null or an empty {@link String},
     * then the service's host machine public IP will be used instead.
     *
     * @param ip the given IP address to have its city/state location fetched. Can be null or empty.
     * @return an {@link IpCityLocation} with the information about the location of the given IP address.
     *
     * @throws me.github.lparo.geolocation.exception.InvalidIpException if the specified IP address is not a valid IPv4 address.
     */
    public IpCityLocation getCityLocation(String ip) {
        ipValidator.validateIp(ip);

        return IpCityLocation.fromDomain(
                ip == null || ip.isEmpty()
                    ? ipCityLocationService.getCityLocationForHostIp()
                    : ipCityLocationService.getCityLocationForIp(ip)
        );
    }

    /**
     * Gets the country information of the given IP address. If the IP is null or an empty {@link String},
     * then the service's host machine public IP will be used instead.
     *
     * @param ip the given IP address to have its country location fetched. Can be null or empty.
     * @return an {@link IpCountryLocation} with the information about the location of the given IP address.
     *
     * @throws me.github.lparo.geolocation.exception.InvalidIpException if the specified IP address is not a valid IPv4 address.
     */
    public IpCountryLocation getCountryLocation(String ip) {
        ipValidator.validateIp(ip);

        return IpCountryLocation.fromDomain(
                ip == null || ip.trim().isEmpty()
                    ? ipCountryLocationService.getCountryLocationForHostIp()
                    : ipCountryLocationService.getCountryLocationForIp(ip)
        );
    }
}
