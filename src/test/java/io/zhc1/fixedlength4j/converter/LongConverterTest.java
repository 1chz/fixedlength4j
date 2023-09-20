package io.zhc1.fixedlength4j.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.zhc1.fixedlength4j.annotation.Align;
import io.zhc1.fixedlength4j.annotation.Fixed;
import io.zhc1.fixedlength4j.annotation.Pad;

class LongConverterTest {
    LongConverter sut = new LongConverter();

    static class TestEntity {
        @Fixed(bytes = 5, order = 1, pad = Pad.ZERO, align = Align.RIGHT)
        private Long value;
    }

    @Test
    @DisplayName("Converts Long to string representation (padding applied by FixedLengthFormat)")
    void test_convertToString() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("value");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        String actual = sut.convertToString(1L, annotation);

        // then
        // Converter returns raw value without padding; padding is applied by FixedLengthFormat
        assertEquals("1", actual);
    }

    @Test
    @DisplayName("Converts fixed-length string to Long")
    void test_convertToObject() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("value");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        Long actual = sut.convertToObject("00001", annotation);

        // then
        assertEquals(1L, actual);
    }
}
