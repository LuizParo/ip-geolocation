package me.github.lparo.geolocation.domain;

import lombok.Value;

/**
 * The container domain that wraps the {@link City} and {@link State} a given IP address is located.
 *
 * @see City
 * @see State
 */
@Value(staticConstructor = "of")
public class IpCityLocation {

    /**
     * the {@link City} information where the IP address is located.
     */
    City city;

    /**
     * the {@link State} information where the IP address is located.
     */
    State state;
}