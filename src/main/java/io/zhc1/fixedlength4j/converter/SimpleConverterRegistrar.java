package io.zhc1.fixedlength4j.converter;

import java.util.Map;

public final class SimpleConverterRegistrar extends ConverterRegistrar {
    @Override
    protected void addConverters(Map<Class<?>, Converter<?>> converters) {
        // No converter other than the default converter has been registered.
    }
}
