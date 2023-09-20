package io.zhc1.fixedlength4j.converter;

import java.math.BigDecimal;

import io.zhc1.fixedlength4j.annotation.Fixed;

final class FloatConverter extends AbstractConverter<Float> {
    @Override
    public String asString(Float value, Fixed annotation) {
        return String.valueOf(value);
    }

    @Override
    public Float asObject(String value, Fixed annotation) {
        return new BigDecimal(value).floatValue();
    }
}
