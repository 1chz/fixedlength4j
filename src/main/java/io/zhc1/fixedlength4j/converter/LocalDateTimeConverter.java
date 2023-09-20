package io.zhc1.fixedlength4j.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.zhc1.fixedlength4j.annotation.Fixed;

final class LocalDateTimeConverter extends AbstractConverter<LocalDateTime> {
    @Override
    public String asString(LocalDateTime value, Fixed annotation) {
        return value.format(DateTimeFormatter.ofPattern(annotation.pattern()));
    }

    @Override
    public LocalDateTime asObject(String value, Fixed annotation) {
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(annotation.pattern()));
    }
}
