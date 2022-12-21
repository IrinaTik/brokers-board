package ru.springtraining;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.springtraining.entity.ExchangeRate;
import ru.springtraining.service.CurrencyService;
import ru.springtraining.service.ExchangeRateService;

import java.util.List;

@SpringBootApplication
public class Main implements CommandLineRunner {

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private ExchangeRateService rateService;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {
        currencyService.getAll().forEach(System.out::println);
        List<ExchangeRate> rates = rateService.getAll();
        rates.forEach(System.out::println);
        rates.stream().map(ExchangeRate::latestExchangeRateToString).forEach(System.out::println);
    }

}
