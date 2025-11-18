package info.globe.countries.helper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.globe.countries.dto.CountryInfo;
import info.globe.countries.exception.CountryNameNotValidException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Component
public class CountryWebClient {

    private final RestClient restClient;
    private static final String BASE_URL= "https://restcountries.com/v3.1/name/";
    private final ObjectMapper objectMapper;
    public CountryWebClient(RestClient.Builder builder,ObjectMapper objectMapper){
        this.restClient = builder.baseUrl(BASE_URL).build();
        this.objectMapper = objectMapper;
    }

    public Optional<CountryInfo> getInfoAboutCountry(String countryName){
        List<CountryInfo> countryInfos = restClient.get()
               .uri("{countryName}",countryName)
               .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,((request, response) -> {
                    throw new CountryNameNotValidException(countryName);
                }))
               .body(new ParameterizedTypeReference<List<CountryInfo>>() {});
        if(countryInfos==null)return Optional.empty();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return Optional.of(objectMapper.convertValue(countryInfos.get(0), CountryInfo.class));
    }

}