package info.globe.countries.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.globe.countries.exception.CountryNotFoundException;
import info.globe.countries.model.Country;
import info.globe.countries.service.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CountryService countryService;

    @Autowired
    private ObjectMapper objectMapper;

    String countryName;
    String capital;
    int population;
    Country country;

    @BeforeEach
    public void init(){
        countryName  = "Malaysia";
        capital  = "Kuala Lumpur";
        population = 34231700;
        country = new Country();
        country.setName(countryName);
        country.setPopulation(population);
        country.setCapital(capital);
    }
    @Test
    public void testSearchInfoForCountry() throws Exception{
        when(countryService.getInfo(countryName)).thenReturn(country);

        MvcResult mvcResult =  mockMvc.perform(get("/country/{countryName}",countryName).contentType(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted()).andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(countryName))
                .andExpect(jsonPath("$.population").value(population))
                .andExpect(jsonPath("$.capital").value(capital));
    }


    @Test
    public void testSearchInfoForCountryWhenException() throws Exception{
        when(countryService.getInfo(countryName)).thenThrow(new RuntimeException());

        MvcResult mvcResult =  mockMvc.perform(get("/country/{countryName}",countryName).contentType(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted()).andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError());

    }


    @Test
    public void testDeleteCountry() throws Exception {
        doNothing().when(countryService).delete(countryName);

        MvcResult mvcResult =  mockMvc.perform(delete("/country/{countryName}",countryName).contentType(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted()).andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteCountryWhenCountryNotFoundException() throws Exception {
        doThrow(CountryNotFoundException.class).when(countryService).delete(countryName);

        MvcResult mvcResult =  mockMvc.perform(delete("/country/{countryName}",countryName).contentType(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted()).andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateCountryInfo() throws Exception{
        when(countryService.updateCountry(any(Country.class))).thenReturn(country);

        MvcResult mvcResult =  mockMvc.perform(patch("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(country)))
                .andExpect(request().asyncStarted()).andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.population").value(population))
                .andExpect(jsonPath("$.capital").value(capital));
    }

    @Test
    public void testGetAllCountries() throws Exception {
        Pageable pageable = PageRequest.of(0,2);
        List<Country> list = new ArrayList<>();
        list.add(country);
        Page<Country> page= new PageImpl<>(list,pageable,list.size());
        when(countryService.getAllCountryPage(anyInt(),anyInt())).thenReturn(page);

        mockMvc.perform(get("/country/page/{pageNumber}/size/{pageSize}",0,2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value(countryName))
                .andExpect(jsonPath("$.content[0].capital").value(capital))
                .andExpect(jsonPath("$.content[0].population").value(population));
    }
}
