package info.globe.countries.exception;

public class CountryNotFoundException extends RuntimeException{
    public CountryNotFoundException(String name){
        super("Country [name:"+name+"] not found");
    }
}
