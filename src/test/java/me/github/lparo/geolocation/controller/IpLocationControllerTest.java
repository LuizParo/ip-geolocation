package me.github.lparo.geolocation.controller;

import me.github.lparo.geolocation.database.IpCityLocationRepository;
import me.github.lparo.geolocation.database.IpCountryLocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class IpLocationControllerTest {
    private static final String ENDPOINT = "/geolocation/ips";
    private static final String VALID_IP = "217.138.219.147";
    private static final String INVALID_IP = "invalid";
    private static final String IP_WITHOUT_LOCATION = "127.0.0.1";

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private IpCityLocationRepository ipCityLocationRepository;

    @SpyBean
    private IpCountryLocationRepository ipCountryLocationRepository;

    @Test
    void getCityLocation_whenIpIsSpecified_shouldUseReturnItsCityLocation() throws Exception {
        this.mockMvc.perform(get(ENDPOINT + "/city").queryParam("ip", VALID_IP))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city.name", is("Milan")))
                .andExpect(jsonPath("$.city.geoNameId", is(3173435)))
                .andExpect(jsonPath("$.state.name", is("Milan")))
                .andExpect(jsonPath("$.state.geoNameId", is(3173434)))
                .andExpect(jsonPath("$.state.isoCode", is("MI")));
    }

    @Test
    void getCityLocation_whenIpIsNotSpecified_shouldUseCurrentHostExternalIp() throws Exception {
        this.mockMvc.perform(get(ENDPOINT + "/city"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city.name", not(emptyOrNullString())))
                .andExpect(jsonPath("$.city.geoNameId", notNullValue()))
                .andExpect(jsonPath("$.state.name", not(emptyOrNullString())))
                .andExpect(jsonPath("$.state.geoNameId", notNullValue()))
                .andExpect(jsonPath("$.state.isoCode", not(emptyOrNullString())));
    }

    @Test
    void getCityLocation_whenIpIsSpecified_andItIsInvalid_shouldReturnBadRequestHttpStatus() throws Exception {
        this.mockMvc.perform(get(ENDPOINT + "/city").queryParam("ip", INVALID_IP))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("invalid IP format: " + INVALID_IP)));
    }

    @Test
    void getCityLocation_whenIpIsSpecified_andItIsDoesNotResolveToAnyLocation_shouldReturnNotFoundHttpStatus() throws Exception {
        this.mockMvc.perform(get(ENDPOINT + "/city").queryParam("ip", IP_WITHOUT_LOCATION))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("unable to find city location for IP " + IP_WITHOUT_LOCATION)));
    }

    @Test
    void getCityLocation_whenAnUnknownExceptionIsThrown_shouldReturnInternalServerErrorHttpStatus() throws Exception {
        final String expectedMessage = "unknown exception";

        doThrow(new RuntimeException(expectedMessage)).when(ipCityLocationRepository).getCityLocationForIp(VALID_IP);

        this.mockMvc.perform(get(ENDPOINT + "/city").queryParam("ip", VALID_IP))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    @Test
    void getCountryLocation_whenIpIsSpecified_shouldUseReturnItsCCountryLocation() throws Exception {
        this.mockMvc.perform(get(ENDPOINT + "/country").queryParam("ip", VALID_IP))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country.name", is("Italy")))
                .andExpect(jsonPath("$.country.geoNameId", is(3175395)))
                .andExpect(jsonPath("$.country.inEuropeanUnion", is(TRUE)))
                .andExpect(jsonPath("$.country.isoCode", is("IT")));
    }

    @Test
    void getCountryLocation_whenIpIsNotSpecified_shouldUseCurrentHostExternalIp() throws Exception {
        this.mockMvc.perform(get(ENDPOINT + "/country"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country.name", not(emptyOrNullString())))
                .andExpect(jsonPath("$.country.geoNameId", notNullValue()))
                .andExpect(jsonPath("$.country.inEuropeanUnion", notNullValue()))
                .andExpect(jsonPath("$.country.isoCode", not(emptyOrNullString())));
    }

    @Test
    void getCountryLocation_whenIpIsSpecified_andItIsInvalid_shouldReturnBadRequestHttpStatus() throws Exception {
        this.mockMvc.perform(get(ENDPOINT + "/country").queryParam("ip", INVALID_IP))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("invalid IP format: " + INVALID_IP)));
    }

    @Test
    void getCountryLocation_whenIpIsSpecified_andItIsDoesNotResolveToAnyLocation_shouldReturnNotFoundHttpStatus() throws Exception {
        this.mockMvc.perform(get(ENDPOINT + "/country").queryParam("ip", IP_WITHOUT_LOCATION))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("unable to find country location for IP " + IP_WITHOUT_LOCATION)));
    }

    @Test
    void getCountryLocation_whenAnUnknownExceptionIsThrown_shouldReturnInternalServerErrorHttpStatus() throws Exception {
        final String expectedMessage = "unknown exception";

        doThrow(new RuntimeException(expectedMessage)).when(ipCountryLocationRepository).getCountryLocationForIp(VALID_IP);

        this.mockMvc.perform(get(ENDPOINT + "/country").queryParam("ip", VALID_IP))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }
}
