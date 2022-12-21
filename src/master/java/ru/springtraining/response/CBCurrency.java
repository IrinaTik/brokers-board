package ru.springtraining.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Lombok
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class CBCurrency {

    private String id;
    private String numcode;
    private String charcode;
    private Integer nominal;
    private String name;
    private Double value;
    private Double previous;

    @Override
    public String toString() {
        return "Currency (id = " + id +
                ") name = '" + name + '\'' +
                ", code = " + charcode +
                ", value = " + value;
    }
}

