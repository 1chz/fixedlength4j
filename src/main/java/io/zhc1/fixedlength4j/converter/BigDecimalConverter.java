package io.zhc1.fixedlength4j.converter;

import java.math.BigDecimal;

import io.zhc1.fixedlength4j.annotation.Fixed;

final class BigDecimalConverter extends AbstractConverter<BigDecimal> {
    @Override
    protected String asString(BigDecimal value, Fixed annotation) {
        return value.toString();
    }

    @Override
    protected BigDecimal asObject(String value, Fixed annotation) {
        return new BigDecimal(value);
    }
}
