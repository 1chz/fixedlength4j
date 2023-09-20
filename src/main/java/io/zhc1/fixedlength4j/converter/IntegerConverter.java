package io.zhc1.fixedlength4j.converter;

import io.zhc1.fixedlength4j.annotation.Fixed;

final class IntegerConverter extends AbstractConverter<Integer> {
    @Override
    public String asString(Integer value, Fixed annotation) {
        return String.valueOf(value);
    }

    @Override
    public Integer asObject(String value, Fixed annotation) {
        return Integer.parseInt(value);
    }
}
