package io.zhc1.fixedlength4j.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.math.BigInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.zhc1.fixedlength4j.annotation.Align;
import io.zhc1.fixedlength4j.annotation.Fixed;
import io.zhc1.fixedlength4j.annotation.Pad;

class BigIntegerConverterTest {
    BigIntegerConverter sut = new BigIntegerConverter();

    static class TestEntity {
        @Fixed(bytes = 5, order = 1, pad = Pad.ZERO, align = Align.RIGHT)
        private BigInteger value;
    }

    @Test
    @DisplayName("Converts BigInteger to string representation (padding applied by FixedLengthFormat)")
    void test_convertToString() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("value");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        String actual = sut.convertToString(BigInteger.ONE, annotation);

        // then
        // Converter returns raw value without padding; padding is applied by FixedLengthFormat
        assertEquals("1", actual);
    }

    @Test
    @DisplayName("Converts fixed-length string to BigInteger")
    void test_convertToObject() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("value");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        BigInteger actual = sut.convertToObject("00001", annotation);

        // then
        assertEquals(BigInteger.ONE, actual);
    }
}
