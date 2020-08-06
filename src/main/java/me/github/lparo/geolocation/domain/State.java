package me.github.lparo.geolocation.domain;

import lombok.Value;

/**
 * Entity that contains information about the state/province/etc a given IP address is located.
 */
@Value(staticConstructor = "of")
public class State {

    /**
     * the name of the state.
     */
    String name;

    /**
     * the geo name id of the state.
     */
    int geoNameId;

    /**
     * the iso code of the state.
     */
    String isoCode;
}
