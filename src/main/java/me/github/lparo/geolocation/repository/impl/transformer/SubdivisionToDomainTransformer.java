package me.github.lparo.geolocation.repository.impl.transformer;

import com.maxmind.geoip2.record.Subdivision;
import me.github.lparo.geolocation.domain.State;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Transformer responsible for converting a {@link com.maxmind.geoip2.record.Subdivision} instance into a {@link me.github.lparo.geolocation.domain.State}
 * domain instance.
 */
@Component
public class SubdivisionToDomainTransformer implements Function<com.maxmind.geoip2.record.Subdivision, State> {

    /**
     * Converts a {@link com.maxmind.geoip2.record.Subdivision} instance into a {@link me.github.lparo.geolocation.domain.State}
     * domain instance.
     *
     * @param subdivision the {@link com.maxmind.geoip2.record.Subdivision} instance to be converted to its domain counterpart.
     *
     * @return the converted {@link me.github.lparo.geolocation.domain.State} domain instance.
     */
    @Override
    public State apply(Subdivision subdivision) {
        return State.of(subdivision.getName(), subdivision.getGeoNameId(), subdivision.getIsoCode());
    }
}
