package info.globe.countries.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Table(name = "language",indexes = @Index(name = "index_language_name",columnList = "name"))
@Entity
public class Language implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    private String  name;

    @JsonIgnore
    @ManyToMany(mappedBy = "languages")
    private Set<Country> country = new HashSet<>();

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

    public Set<Country> getCountry() {
        return country;
    }

    public void setCountry(Set<Country> country) {
        this.country = country;
    }


}
