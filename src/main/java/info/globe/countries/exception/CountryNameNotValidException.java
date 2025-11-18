package info.globe.countries.exception;

public class CountryNameNotValidException extends RuntimeException{

    public CountryNameNotValidException(String searchVal){
        super("CountryName is not valid for the searchVal:"+searchVal);
    }
}
