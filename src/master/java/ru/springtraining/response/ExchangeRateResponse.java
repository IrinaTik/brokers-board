package ru.springtraining.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.springtraining.entity.Currency;

import java.time.LocalDate;

// Lombok
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateResponse {

    private Currency firstCurrency;
    private Currency secondCurrency;
    private Integer firstCurrencyValue;
    private Double secondCurrencyValue;
    private LocalDate date;

}
