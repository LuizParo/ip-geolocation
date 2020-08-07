package me.github.lparo.geolocation.repository.impl.geoip2;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import me.github.lparo.geolocation.repository.impl.transformer.IpCountryLocationTransformer;
import me.github.lparo.geolocation.domain.Country;
import me.github.lparo.geolocation.domain.IpCountryLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetAddress;
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
class GeoIP2IpCountryLocationRepositoryTest {
    private static final String IP = "217.138.219.147";

    private static final String NAME = UUID.randomUUID().toString();
    private static final int GEO_NAME_ID = ThreadLocalRandom.current().nextInt();
    private static final boolean IS_IN_EUROPEAN_UNION = ThreadLocalRandom.current().nextBoolean();
    private static final String ISO_CODE = UUID.randomUUID().toString();

    @InjectMocks
    private GeoIP2IpCountryLocationRepository ipCountryLocationRepository;

    @Mock
    private DatabaseReader databaseReader;

    @Mock
    private IpCountryLocationTransformer ipCountryLocationTransformer;

    @Test
    void getCountryLocationForIp_whenALocationIsFoundForTheGivenIp_shouldReturnItInAnOptional() throws Exception {
        final InetAddress ipAddress = InetAddress.getByName(IP);

        final CountryResponse response = createResponse();
        final IpCountryLocation expectedIpCountryLocation = createIpCountryLocation();

        when(databaseReader.tryCountry(ipAddress)).thenReturn(Optional.of(response));
        when(ipCountryLocationTransformer.apply(response)).thenReturn(expectedIpCountryLocation);

        final Optional<IpCountryLocation> ipCountryLocation = ipCountryLocationRepository.getCountryLocationForIp(IP);

        verify(databaseReader, times(1)).tryCountry(ipAddress);
        verify(ipCountryLocationTransformer, times(1)).apply(response);

        verifyNoMoreInteractions(databaseReader);
        verifyNoMoreInteractions(ipCountryLocationTransformer);

        assertThat(ipCountryLocation.isPresent(), is(TRUE));
        assertThat(ipCountryLocation, is(Optional.of(expectedIpCountryLocation)));
    }

    @Test
    void getCountryLocationForIp_whenALocationIsNotFoundForTheGivenIp_shouldReturnOptionalEmpty() throws Exception {
        final InetAddress ipAddress = InetAddress.getByName(IP);

        when(databaseReader.tryCountry(ipAddress)).thenReturn(Optional.empty());

        final Optional<IpCountryLocation> ipCountryLocation = ipCountryLocationRepository.getCountryLocationForIp(IP);

        verify(databaseReader, times(1)).tryCountry(ipAddress);

        verifyNoMoreInteractions(databaseReader);
        verifyNoMoreInteractions(ipCountryLocationTransformer);

        assertThat(ipCountryLocation.isPresent(), is(FALSE));
    }

    @Test
    void getCountryLocationForIp_whenAnExceptionIsThrown_shouldPropagateItAsARuntimeException() throws Exception {
        final String expectedMessage = "unknown exception";
        final InetAddress ipAddress = InetAddress.getByName(IP);

        when(databaseReader.tryCountry(ipAddress)).thenThrow(new IllegalStateException(expectedMessage));

        try {
            assertThrows(RuntimeException.class, () -> ipCountryLocationRepository.getCountryLocationForIp(IP), expectedMessage);
        } finally {
            verify(databaseReader, times(1)).tryCountry(ipAddress);

            verifyNoMoreInteractions(databaseReader);
            verifyNoInteractions(ipCountryLocationTransformer);
        }
    }

    private CountryResponse createResponse() {
        final com.maxmind.geoip2.record.Country country = new com.maxmind.geoip2.record.Country(
                singletonList(Locale.US.getLanguage()),
                ThreadLocalRandom.current().nextInt(),
                GEO_NAME_ID,
                IS_IN_EUROPEAN_UNION,
                ISO_CODE,
                singletonMap(Locale.US.getLanguage(), NAME)
        );

        return new CountryResponse(null, country, null, null, null, null);
    }

    public IpCountryLocation createIpCountryLocation() {
        return IpCountryLocation.of(Country.of(NAME, GEO_NAME_ID, IS_IN_EUROPEAN_UNION, ISO_CODE));
    }
}