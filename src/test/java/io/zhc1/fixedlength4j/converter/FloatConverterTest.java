package io.zhc1.fixedlength4j.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.zhc1.fixedlength4j.annotation.Align;
import io.zhc1.fixedlength4j.annotation.Fixed;
import io.zhc1.fixedlength4j.annotation.Pad;

class FloatConverterTest {
    FloatConverter sut = new FloatConverter();

    static class TestEntity {
        @Fixed(bytes = 5, order = 1, pad = Pad.ZERO, align = Align.RIGHT)
        private Float value;
    }

    @Test
    @DisplayName("Converts Float to string representation (padding applied by FixedLengthFormat)")
    void test_convertToString() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("value");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        String actual = sut.convertToString(1.01F, annotation);

        // then
        // Converter returns raw value without padding; padding is applied by FixedLengthFormat
        assertEquals("1.01", actual);
    }

    @Test
    @DisplayName("Converts fixed-length string to Float")
    void test_convertToObject() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("value");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        Float actual = sut.convertToObject("01.01", annotation);

        // then
        assertEquals(1.01F, actual);
    }
}
