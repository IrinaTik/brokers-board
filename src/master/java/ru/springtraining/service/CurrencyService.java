package ru.springtraining.service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.springtraining.entity.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.springtraining.repository.CurrencyRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    public List<Currency> getAll() {
        return currencyRepository.findAll();
    }

    public Currency getById(int id) {
        return currencyRepository.findById(id).orElse(null);
    }

    public Currency add(Currency currency){
        return currencyRepository.saveAndFlush(currency);
    }

    public Currency findByCode(String code) {
        return currencyRepository.findByCode(code);
    }
}
