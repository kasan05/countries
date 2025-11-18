package info.globe.countries.service;

import info.globe.countries.dto.CountryInfo;
import info.globe.countries.dto.NameInfo;
import info.globe.countries.helper.CountryWebClient;
import info.globe.countries.model.Country;
import info.globe.countries.model.Language;
import info.globe.countries.repository.CountryRepository;
import info.globe.countries.repository.LanguageRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class CountryServiceTest {

    @InjectMocks
    private CountryService countryService;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountryWebClient countryWebClient;

    @Mock
    private LanguageRepository languageRepository;
    String countryName;
    String capital;
    int populationTotal;
    Country country;
    Optional<CountryInfo> countryInfoOptional;
    List<Language> existingLanguageList;

    @BeforeEach
    public void init() {
        countryName = "Malaysia";
        capital = "Kuala Lumpur";
        populationTotal = 34231700;
        country = new Country();
        country.setName(countryName);
        country.setPopulation(populationTotal);
        country.setCapital(capital);
        NameInfo name =  new NameInfo(countryName);
        Map<String,Object> languages = new HashMap<>();
        languages.put("eng","English");
        languages.put("msa","Malay");
        List<Object> capitalList = new ArrayList<>();
        capitalList.add(capital);
        Object population = populationTotal;
        CountryInfo countryInfo = new CountryInfo(name,languages,capitalList,population);
        countryInfoOptional = Optional.of(countryInfo);
        existingLanguageList = new ArrayList<>();
        Language language = new Language();
        language.setId(1);
        language.setName("English");
        Language language2 = new Language();
        language2.setId(1);
        language2.setName("Malay");
        existingLanguageList.add(language2);
        existingLanguageList.add(language);
        country.setLanguages(new HashSet<>(existingLanguageList));
    }

    @Test
    public void testGetInfoIfCountryExistsInCache() {
        when(countryRepository.findByName(anyString())).thenReturn(country);
        Country countryInfo = countryService.getInfo(countryName);
        assertEquals(countryName, countryInfo.getName());
        assertEquals(capital, countryInfo.getCapital());
        assertEquals(populationTotal, countryInfo.getPopulation());
        verify(countryRepository, times(1)).findByName(anyString());
    }

    @Test
    public void testGetInfoIfCountryIfNotExistsInCache() {
        when(countryRepository.findByName(anyString())).thenReturn(null);
        when(countryWebClient.getInfoAboutCountry(anyString())).thenReturn(countryInfoOptional);
        when(languageRepository.findByNameIn(anyList())).thenReturn(existingLanguageList);
        when(countryRepository.save(any(Country.class))).thenReturn(country);

        Country countryInfo = countryService.getInfo(countryName);

        assertEquals(countryInfoOptional.get().name().common(),countryInfo.getName());
        assertEquals(countryInfoOptional.get().capital().get(0),capital);
        assertEquals(countryInfoOptional.get().population(),populationTotal);
    }

}