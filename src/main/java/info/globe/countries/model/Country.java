package info.globe.countries.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Table(name = "country",indexes = @Index(name="index_country_name",columnList = "name"))
@Entity
public class Country implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    private String name;

    public Country(){}
    public Country(String name, String capital, Integer population) {
        this.name = name;
        this.capital = capital;
        this.population = population;
    }

    private String capital;

    private Integer population;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "country_language",joinColumns = @JoinColumn(name = "country_id"),
    inverseJoinColumns = @JoinColumn(name = "language_id"))
    private Set<Language> languages = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }
    public Set<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<Language> languages) {
        this.languages = languages;
    }
    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    @Override
    public String toString() {
        return new StringBuilder("Country[")
                .append(this.getName())
                .append(",")
                .append(this.getCapital())
                .append(",")
                .append(this.getPopulation())
                .append("]")
                .toString();
    }
}
