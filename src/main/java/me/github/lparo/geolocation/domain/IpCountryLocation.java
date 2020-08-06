package me.github.lparo.geolocation.domain;

import lombok.Value;

/**
 * The container domain that wraps the {@link Country} a given IP address is located.
 *
 * @see Country
 */
@Value(staticConstructor = "of")
public class IpCountryLocation {

    /**
     * the {@link Country} information where the IP address is located.
     */
    Country country;
}
