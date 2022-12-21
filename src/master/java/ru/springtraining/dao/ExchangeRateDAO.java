package ru.springtraining.dao;

import ru.springtraining.entity.ExchangeRate;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExchangeRateDAO implements GeneralDAO<ExchangeRate> {

    private static List<ExchangeRate> rates = new ArrayList<>();

    @Override
    public List<ExchangeRate> getAll() throws SQLException {
        return rates;
    }

    @Override
    public ExchangeRate getById(int id) throws SQLException {
        return null;
    }

    @Override
    public boolean add(ExchangeRate rate) throws SQLException {
        return rates.add(rate);
    }

    @Override
    public void update(String query) throws SQLException {

    }

    @Override
    public void delete(String query) throws SQLException {

    }
}
