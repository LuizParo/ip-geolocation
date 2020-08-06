package me.github.lparo.geolocation.database.impl.transformer;

import me.github.lparo.geolocation.domain.Country;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Transformer responsible for converting a {@link com.maxmind.geoip2.record.Country} instance into a {@link me.github.lparo.geolocation.domain.Country}
 * domain instance.
 */
@Component
public class CountryToDomainTransformer implements Function<com.maxmind.geoip2.record.Country, Country> {

    /**
     * Converts a {@link com.maxmind.geoip2.record.Country} instance into a {@link me.github.lparo.geolocation.domain.Country}
     * domain instance.
     *
     * @param country the {@link com.maxmind.geoip2.record.Country} instance to be converted to its domain counterpart.
     *
     * @return the converted {@link me.github.lparo.geolocation.domain.Country} domain instance.
     */
    @Override
    public Country apply(com.maxmind.geoip2.record.Country country) {
        return Country.of(
                country.getName(),
                country.getGeoNameId(),
                country.isInEuropeanUnion(),
                country.getIsoCode()
        );
    }
}
