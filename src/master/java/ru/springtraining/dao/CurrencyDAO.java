package ru.springtraining.dao;

import ru.springtraining.entity.Currency;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CurrencyDAO implements GeneralDAO<Currency> {

    private static List<Currency> currencies = new ArrayList<>();

    @Override
    public List<Currency> getAll() throws SQLException {
        return currencies;
    }

    @Override
    public Currency getById(int id) throws SQLException {
        return currencies.get(id);
    }

    @Override
    public boolean add(Currency currency) throws SQLException {
        return currencies.add(currency);
    }

    @Override
    public void update(String query) throws SQLException {

    }

    @Override
    public void delete(String query) throws SQLException {

    }
}
