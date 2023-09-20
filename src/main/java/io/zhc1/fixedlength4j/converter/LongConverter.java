package io.zhc1.fixedlength4j.converter;

import io.zhc1.fixedlength4j.annotation.Fixed;

final class LongConverter extends AbstractConverter<Long> {
    @Override
    public String asString(Long value, Fixed annotation) {
        return String.valueOf(value);
    }

    @Override
    public Long asObject(String value, Fixed annotation) {
        return Long.valueOf(value);
    }
}
