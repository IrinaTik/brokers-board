package ru.springtraining.dao;

import java.sql.SQLException;
import java.util.List;

public interface GeneralDAO<T> {

    List<T> getAll() throws SQLException;

    T getById(int id) throws SQLException;

    boolean add(T object) throws SQLException;

    void update(String query) throws SQLException;

    void delete(String query) throws SQLException;

}
