package me.github.lparo.geolocation.controller;

import lombok.AllArgsConstructor;
import me.github.lparo.geolocation.api.IpLocationApi;
import me.github.lparo.geolocation.controller.dto.IpCityLocation;
import me.github.lparo.geolocation.controller.dto.IpCountryLocation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for providing the HTTP endpoints to fetch the city/state/country information of where a given IP
 * address is hosted.
 *
 * @see me.github.lparo.geolocation.controller.exception.IpLocationControllerAdvice
 */
@RestController
@RequestMapping("geolocation/ips")
@AllArgsConstructor
public class IpLocationController {
    private final IpLocationApi ipLocationApi;

    /**
     * Endpoint that fetches the city/state information of where a given IP address is hosted. The IP address is specified
     * in the request's query parameter. If it's absent, then the external IP of the service's host machine will be used
     * instead.
     *
     * @param ip the given IP address to have its city/state location fetched. It's specified as an optional query parameter.
     *
     * @return the {@link ResponseEntity<IpCityLocation>} with the IP address city/state in the response body.
     */
    @GetMapping(value = "/city")
    public ResponseEntity<IpCityLocation> getCityLocation(@RequestParam(required = false) String ip) {
        return ResponseEntity.ok(ipLocationApi.getCityLocation(ip));
    }

    /**
     * Endpoint that fetches the country information of where a given IP address is hosted. The IP address is specified
     * in the request's query parameter. If it's absent, then the external IP of the service's host machine will be used
     * instead.
     *
     * @param ip the given IP address to have its country location fetched. It's specified as an optional query parameter.
     *
     * @return the {@link ResponseEntity<IpCountryLocation>} with the IP address country in the response body.
     */
    @GetMapping(value = "/country")
    public ResponseEntity<IpCountryLocation> getCountryLocation(@RequestParam(required = false) String ip) {
        return ResponseEntity.ok(ipLocationApi.getCountryLocation(ip));
    }
}