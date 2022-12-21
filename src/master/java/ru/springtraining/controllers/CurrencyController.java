package ru.springtraining.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.springtraining.entity.Currency;
import ru.springtraining.service.CurrencyService;

import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CurrencyController {

    private final CurrencyService service;

    @GetMapping("/currencies")
    public ResponseEntity getAllRates() {
        List<Currency> currencies = service.getAll();
        if (currencies.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
        return new ResponseEntity(currencies, HttpStatus.OK);
    }

    @GetMapping("/currencies/{code}")
    public ResponseEntity getCurrencyByCode(@PathVariable String code) {
        Currency currency = service.findByCode(code);
        if (currency == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
        return new ResponseEntity<>(currency, HttpStatus.OK);
    }
}
