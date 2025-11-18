package info.globe.countries.service;

import info.globe.countries.dto.CountryInfo;
import info.globe.countries.dto.NameInfo;
import info.globe.countries.exception.CountryNotFoundException;
import info.globe.countries.helper.CountryWebClient;
import info.globe.countries.model.Country;
import info.globe.countries.model.Language;
import info.globe.countries.repository.CountryRepository;
import info.globe.countries.repository.LanguageRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    private final LanguageRepository languageRepository;

    private final CountryWebClient countryWebClient;

    public CountryService(final CountryRepository countryRepository,
                          CountryWebClient countryWebClient,
                          LanguageRepository languageRepository){
        this.countryRepository = countryRepository;
        this.countryWebClient = countryWebClient;
        this.languageRepository = languageRepository;
    }

    public Country getInfo(String countryName) throws RuntimeException{
        //retrieve from cache
        Country countryFromCache = findByName(countryName);
        if(countryFromCache!=null){
            return countryFromCache;
        }
       Optional<CountryInfo> countryInfoOptional = countryWebClient.getInfoAboutCountry(countryName);
       if(countryInfoOptional.isEmpty()) throw new RuntimeException();
       CountryInfo countryInfo = countryInfoOptional.get();
       NameInfo nameInfo = countryInfo.name();
       String name = (String) nameInfo.common();
       int population = (int)countryInfo.population();
       String capital = (String) countryInfo.capital().get(0);
       Country country = new Country(name,capital,population);
       List<String> languageNamesFromCountryInfo = countryInfo.languages().values()
                .stream()
                .map(x->(String)x)
                .toList();
       return saveCountryWithLanguages(country,languageNamesFromCountryInfo);
    }

    @CachePut(value ="countryByName",key = "#country.name")
    public Country saveCountry(Country country){
        return countryRepository.save(country);
    }

    @Transactional
    public Country saveCountryWithLanguages(Country country,List<String> languageNamesFromCountryInfo){
        List<Language> alreadyExistingLanguages = getAllLanguageByNames(languageNamesFromCountryInfo);

        final  List<String> alreadyExistingLanguagesNames;
        if(alreadyExistingLanguages!=null){
            alreadyExistingLanguagesNames = alreadyExistingLanguages.stream()
                    .map(Language::getName)
                    .collect(Collectors.toList());
        }else{
            alreadyExistingLanguagesNames = null;
        }
        List<Language> nonExistingLanguages = languageNamesFromCountryInfo.stream()
                .filter(lName -> alreadyExistingLanguagesNames!=null
                        && !alreadyExistingLanguagesNames.contains((lName)))
                .map(l->{
                    Language language = new Language();
                    language.setName(l);
                    return language;
                }).collect(Collectors.toList());

        if(nonExistingLanguages!=null && !nonExistingLanguages.isEmpty()){
            nonExistingLanguages = languageRepository.saveAll(nonExistingLanguages);
            if(alreadyExistingLanguages!=null && !alreadyExistingLanguages.isEmpty()){
                nonExistingLanguages.addAll(alreadyExistingLanguages);
            }
            country.setLanguages(new HashSet<>(nonExistingLanguages));
        }else{
            country.setLanguages(new HashSet<>(alreadyExistingLanguages));
        }
        return saveCountry(country);
    }

    public List<Country> findAll(){
        return  countryRepository.findAll();
    }
    @Cacheable(value = "countryByName",key = "#name")
    public Country findByName(String name){
        return countryRepository.findByName(name);
    }

    @Cacheable(value = "languages")
    public List<Language> getAllLanguageByNames(List<String> names){
        return languageRepository.findByNameIn(names);
    }

    @CacheEvict(value = {"countryByName","countries"},key="#countryName")
    public void delete(String countryName){
        Country country =  findByName(countryName);
        if(country==null) throw new CountryNotFoundException(countryName);
        countryRepository.delete(country);
    }

    @CachePut(value = {"countryByName","countries"},key="#country.name")
    public Country update(Country country){
        return countryRepository.save(country);
    }

    public Country updateCountry(Country country){
        String capital = country.getCapital();
        int population = country.getPopulation();
        country = findByName(country.getName());
        if(country==null) throw  new CountryNotFoundException(country.getName());
        country.setPopulation(population);
        country.setCapital(capital);
        return update(country);
    }

    @Cacheable(value = "countries")
    public Page<Country> getAll(int start,int pageSize){
       return countryRepository.findAll(PageRequest.of(start,pageSize));
    }

    public Page<Country> getAllCountryPage(int start,int pageSize){
        return getAll(start,pageSize);
    }

}
