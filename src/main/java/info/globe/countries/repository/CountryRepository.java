package info.globe.countries.repository;

import info.globe.countries.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country,Integer> {
    Country findByName(String name);
}
