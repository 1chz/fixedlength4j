package io.zhc1.fixedlength4j.converter;

import java.math.BigDecimal;

import io.zhc1.fixedlength4j.annotation.Fixed;

final class DoubleConverter extends AbstractConverter<Double> {
    @Override
    public String asString(Double value, Fixed annotation) {
        return String.valueOf(value);
    }

    @Override
    public Double asObject(String value, Fixed annotation) {
        return new BigDecimal(value).doubleValue();
    }
}
