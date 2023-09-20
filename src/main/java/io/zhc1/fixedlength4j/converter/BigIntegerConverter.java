package io.zhc1.fixedlength4j.converter;

import java.math.BigInteger;

import io.zhc1.fixedlength4j.annotation.Fixed;

final class BigIntegerConverter extends AbstractConverter<BigInteger> {
    @Override
    protected String asString(BigInteger value, Fixed annotation) {
        return value.toString();
    }

    @Override
    protected BigInteger asObject(String value, Fixed annotation) {
        return new BigInteger(value);
    }
}
