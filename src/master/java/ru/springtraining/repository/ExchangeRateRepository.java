package ru.springtraining.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.springtraining.entity.Currency;
import ru.springtraining.entity.ExchangeRate;

import java.util.List;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Integer> {

    default ExchangeRate getByCurrencies(Currency first, Currency second) {
        List<ExchangeRate> rates = findAll();
        for (ExchangeRate rate : rates) {
            if (rate.getFirstCurrency().equals(first) && rate.getSecondCurrency().equals(second)) {
                return rate;
            }
        }
        return null;
    }

}
