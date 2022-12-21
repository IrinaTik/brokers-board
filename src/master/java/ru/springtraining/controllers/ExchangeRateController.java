package ru.springtraining.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.springtraining.response.ExchangeRateResponse;
import ru.springtraining.entity.ExchangeRate;
import ru.springtraining.service.ExchangeRateService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExchangeRateController {

    private final ExchangeRateService service;

    @GetMapping("/rates")
    public ResponseEntity getAllRates() {
        List<ExchangeRate> rates = service.getAll();
        if (rates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
        return new ResponseEntity(rates, HttpStatus.OK);
    }

    // дата должна быть в формате YYYY-MM-DD
    @GetMapping("/rates/{firstCurrencyCode}/{secondCurrencyCode}/{date}")
    public ResponseEntity getRateByDateAndCurrencies(@PathVariable String firstCurrencyCode, @PathVariable String secondCurrencyCode, @PathVariable String date) {
        // проверка даты - валидный ввод и не раньше 1.07.1992 (ограничение на сайте ЦБ)
        LocalDate requestDate = service.createDateFromRequest(date);
        if ((requestDate == null) || !service.isDateValid(requestDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date");
        }
        ExchangeRateResponse rateResponse;
        try {
            rateResponse = service.getByCurrenciesAndDate(firstCurrencyCode, secondCurrencyCode, requestDate);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Error while getting data from Central Bank site");
        }
        if (rateResponse == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid currency code");
        }
        return new ResponseEntity(rateResponse, HttpStatus.OK);
    }

    @GetMapping("/rates/{id}")
    public ResponseEntity getRateById (@PathVariable int id) {
        ExchangeRate rate = service.getRateById(id);
        if (rate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
        return new ResponseEntity(rate, HttpStatus.OK);
    }

}
