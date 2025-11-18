package info.globe.countries.repository;

import info.globe.countries.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguageRepository extends JpaRepository<Language,Integer> {
    Language findByName(String name);
    List<Language> findByNameIn(List<String> name);
}
