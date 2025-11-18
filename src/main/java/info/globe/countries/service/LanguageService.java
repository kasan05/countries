package info.globe.countries.service;

import info.globe.countries.model.Language;
import info.globe.countries.repository.LanguageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageService {

    final LanguageRepository languageRepository;

    public LanguageService(LanguageRepository languageRepository){
        this.languageRepository = languageRepository;
    }

    public Language findLanguageByName(String name){
        return languageRepository.findByName(name);
    }
    public List<Language> findLanguageByNames(List<String> names){
        return languageRepository.findByNameIn(names);
    }
}
