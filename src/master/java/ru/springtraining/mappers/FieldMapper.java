package ru.springtraining.mappers;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class FieldMapper {
    public String mapField(String field) {
        return field.toUpperCase();
    }
}
