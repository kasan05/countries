package info.globe.countries;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableAsync
@EnableCaching
public class CountriesApplication {

	public static void main(String[] args) {
		SpringApplication.run(CountriesApplication.class, args);
	}

}
