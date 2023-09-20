package io.zhc1.fixedlength4j.converter;

import io.zhc1.fixedlength4j.annotation.Fixed;

final class BooleanConverter extends AbstractConverter<Boolean> {
    @Override
    public String asString(Boolean value, Fixed annotation) {
        return value ? "1" : "0";
    }

    @Override
    public Boolean asObject(String value, Fixed annotation) {
        if ("1".equals(value)) {
            return Boolean.TRUE;
        } else if ("0".equals(value)) {
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("Invalid boolean value: " + value + ". Expected '1' or '0'.");
    }
}
