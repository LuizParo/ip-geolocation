package me.github.lparo.geolocation.domain;

import lombok.Value;

/**
 * Entity that contains information about the city a given IP address is located.
 */
@Value(staticConstructor = "of")
public class City {

    /**
     * tha name of the city.
     */
    String name;

    /**
     * the geo name id of the city.
     */
    int geoNameId;
}
