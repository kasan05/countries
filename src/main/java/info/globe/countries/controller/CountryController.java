package info.globe.countries.controller;

import info.globe.countries.exception.CountryNameNotValidException;
import info.globe.countries.exception.CountryNotFoundException;
import info.globe.countries.model.Country;
import info.globe.countries.service.CountryService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/country")
public class CountryController {

    final CountryService countryService;

    public CountryController(CountryService countryService){
        this.countryService = countryService;
    }

    @GetMapping("/{name}")
    public CompletableFuture<ResponseEntity<Country>> searchInfoForCountry(@PathVariable("name") String name){
        return CompletableFuture.supplyAsync(()->{
            return countryService.getInfo(name);
        }).thenApply(data->new ResponseEntity<>(data,HttpStatus.OK))
                .exceptionally((throwable)->{
                    if(throwable.getCause() instanceof CountryNameNotValidException){
                       return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }else{
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                });
    }

    @DeleteMapping("/{name}")
    public CompletableFuture<ResponseEntity<Void>>  deleteCountry(@PathVariable("name") String name){
          return  CompletableFuture.runAsync(()->{
              countryService.delete(name);
        }).thenApply(data-> new ResponseEntity<Void>(HttpStatus.OK))
          .exceptionally(throwable -> {
                      if (throwable.getCause() instanceof CountryNotFoundException) {
                          return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
                      }else{
                          return  new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                      }
                  });
    }

    @PatchMapping
    public CompletableFuture<ResponseEntity<Country>> updateCountryInfo(@RequestBody Country country){
        return CompletableFuture.supplyAsync(()->{
           return  countryService.updateCountry(country);
        }).thenApply(data->new ResponseEntity<>(data,HttpStatus.OK));
    }

    @GetMapping("/page/{pageNumber}/size/{pageSize}")
    public ResponseEntity<Page<Country>> getAllCountries(@PathVariable("pageNumber")
                                                            int pageNumber,
                                                            @PathVariable("pageSize") int pageSize){
        return new ResponseEntity<>(countryService.getAllCountryPage(pageNumber,pageSize),HttpStatus.OK);
    }
}
