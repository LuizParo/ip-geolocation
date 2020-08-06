package me.github.lparo.geolocation.domain;

import lombok.Value;

/**
 * Entity that contains information about the country a given IP address is located.
 */
@Value(staticConstructor = "of")
public class Country {

    /**
     * the name of the country.
     */
    String name;

    /**
     * the geo name id of the country.
     */
    int geoNameId;

    /**
     * the flag that identifies if the country is part of EU.
     */
    boolean isInEuropeanUnion;

    /**
     * the iso code of the country.
     */
    String isoCode;
}