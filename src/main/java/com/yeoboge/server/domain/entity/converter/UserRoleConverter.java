package com.yeoboge.server.domain.entity.converter;

import com.yeoboge.server.domain.entity.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class UserRoleConverter implements AttributeConverter<Role, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Role attribute) {
        return attribute == Role.USER ? 1 : 0;
    }

    @Override
    public Role convertToEntityAttribute(Integer dbData) {
        return dbData == 1 ? Role.USER : Role.ADMIN;
    }
}
