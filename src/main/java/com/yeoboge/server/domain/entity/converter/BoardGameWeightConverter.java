package com.yeoboge.server.domain.entity.converter;

import com.yeoboge.server.domain.entity.Weight;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BoardGameWeightConverter implements AttributeConverter<Weight, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Weight attribute) {
        return attribute.getIndex();
    }

    @Override
    public Weight convertToEntityAttribute(Integer dbData) {
        return Weight.valueOf(dbData);
    }
}
