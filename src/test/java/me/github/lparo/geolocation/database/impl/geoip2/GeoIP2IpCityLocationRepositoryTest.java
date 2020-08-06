package me.github.lparo.geolocation.database.impl.geoip2;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Subdivision;
import me.github.lparo.geolocation.database.impl.transformer.IpCityLocationTransformer;
import me.github.lparo.geolocation.domain.City;
import me.github.lparo.geolocation.domain.IpCityLocation;
import me.github.lparo.geolocation.domain.State;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeoIP2IpCityLocationRepositoryTest {
    private static final String IP = "217.138.219.147";

    private static final String CITY_NAME = UUID.randomUUID().toString();
    private static final int CITY_GEO_NAME_ID = ThreadLocalRandom.current().nextInt();

    private static final String STATE_NAME = UUID.randomUUID().toString();
    private static final int STATE_GEO_NAME_ID = ThreadLocalRandom.current().nextInt();
    private static final String ISO_CODE = UUID.randomUUID().toString();

    @InjectMocks
    private GeoIP2IpCityLocationRepository ipCityLocationRepository;

    @Mock
    private DatabaseReader databaseReader;

    @Mock
    private IpCityLocationTransformer ipCityLocationTransformer;

    @Test
    void getCityLocationForIp_whenALocationIsFoundForTheGivenIp_shouldReturnItInAnOptional() throws Exception {
        final InetAddress ipAddress = InetAddress.getByName(IP);

        final CityResponse response = createResponse();
        final IpCityLocation expectedIpCityLocation = createIpCityLocation();

        when(databaseReader.tryCity(ipAddress)).thenReturn(Optional.of(response));
        when(ipCityLocationTransformer.apply(response)).thenReturn(expectedIpCityLocation);

        final Optional<IpCityLocation> ipCityLocation = ipCityLocationRepository.getCityLocationForIp(IP);

        verify(databaseReader, times(1)).tryCity(ipAddress);
        verify(ipCityLocationTransformer, times(1)).apply(response);

        verifyNoMoreInteractions(databaseReader);
        verifyNoMoreInteractions(ipCityLocationTransformer);

        assertThat(ipCityLocation.isPresent(), is(TRUE));
        assertThat(ipCityLocation, is(Optional.of(expectedIpCityLocation)));
    }

    @Test
    void getCityLocationForIp_whenALocationIsNotFoundForTheGivenIp_shouldReturnOptionalEmpty() throws Exception {
        final InetAddress ipAddress = InetAddress.getByName(IP);

        when(databaseReader.tryCity(ipAddress)).thenReturn(Optional.empty());

        final Optional<IpCityLocation> ipCityLocation = ipCityLocationRepository.getCityLocationForIp(IP);

        verify(databaseReader, times(1)).tryCity(ipAddress);

        verifyNoMoreInteractions(databaseReader);
        verifyNoMoreInteractions(ipCityLocationTransformer);

        assertThat(ipCityLocation.isPresent(), is(FALSE));
    }

    @Test
    void getCityLocationForIp_whenAnExceptionIsThrown_shouldPropagateItAsARuntimeException() throws Exception {
        final String expectedMessage = "unknown exception";
        final InetAddress ipAddress = InetAddress.getByName(IP);

        when(databaseReader.tryCity(ipAddress)).thenThrow(new IllegalStateException(expectedMessage));

        try {
            assertThrows(RuntimeException.class, () -> ipCityLocationRepository.getCityLocationForIp(IP), expectedMessage);
        } finally {
            verify(databaseReader, times(1)).tryCity(ipAddress);

            verifyNoMoreInteractions(databaseReader);
            verifyNoInteractions(ipCityLocationTransformer);
        }
    }

    private CityResponse createResponse() {
        final com.maxmind.geoip2.record.City city = new com.maxmind.geoip2.record.City(
                singletonList(Locale.US.getLanguage()),
                ThreadLocalRandom.current().nextInt(),
                CITY_GEO_NAME_ID,
                singletonMap(Locale.US.getLanguage(), CITY_NAME)
        );

        final Subdivision subdivision = new com.maxmind.geoip2.record.Subdivision(
                singletonList(Locale.US.getLanguage()),
                ThreadLocalRandom.current().nextInt(),
                STATE_GEO_NAME_ID,
                ISO_CODE,
                singletonMap(Locale.US.getLanguage(), STATE_NAME)
        );

        final ArrayList<Subdivision> subdivisions = new ArrayList<>();
        subdivisions.add(subdivision);

        return new CityResponse(
                city,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                subdivisions,
                null
        );
    }

    public IpCityLocation createIpCityLocation() {
        return IpCityLocation.of(
                City.of(CITY_NAME, CITY_GEO_NAME_ID),
                State.of(STATE_NAME, STATE_GEO_NAME_ID, ISO_CODE)
        );
    }
}