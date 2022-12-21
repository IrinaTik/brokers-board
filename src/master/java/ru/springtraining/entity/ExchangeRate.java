package ru.springtraining.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

// Lombok
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

// JPA
@Entity
@Table(name = "exchange_rates")
public class ExchangeRate {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "first_currency_id")
    private Currency firstCurrency;

    @OneToOne
    @JoinColumn(name = "second_currency_id")
    private Currency secondCurrency;

    @Column(name = "first_currency_value")
    private Integer firstCurrencyValue;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rate", orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ExchangeRateHistory> history;

    public String latestExchangeRateToString() {
        return "Latest exchange rate for " +
                firstCurrency.getCode() + "/" + secondCurrency.getCode() +
                " is " + firstCurrencyValue + " to " + history.get(history.size() - 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ExchangeRate that = (ExchangeRate) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Exchange rate (id = " + id + ") for " +
                firstCurrency.getCode() + "/" + secondCurrency.getCode() +
                " is " + firstCurrencyValue + " to " + history;
    }
}
