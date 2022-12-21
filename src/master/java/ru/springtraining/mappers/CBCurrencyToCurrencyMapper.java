package ru.springtraining.mappers;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.springtraining.entity.Currency;
import ru.springtraining.response.CBCurrency;

@Mapper(
        componentModel = "spring",
        uses = FieldMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface CBCurrencyToCurrencyMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "code", source = "cbCurrency.charcode")
    })
    Currency cbCurrencyToCurrency(CBCurrency cbCurrency);
}
