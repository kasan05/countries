package info.globe.countries.dto;


import java.util.List;
import java.util.Map;

public record CountryInfo(NameInfo name,
                          Map<String,Object> languages,
                          List<Object> capital,
                          Object population) {}