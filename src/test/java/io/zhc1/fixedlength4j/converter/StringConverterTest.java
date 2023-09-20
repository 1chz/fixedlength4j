package io.zhc1.fixedlength4j.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.zhc1.fixedlength4j.annotation.Align;
import io.zhc1.fixedlength4j.annotation.Fixed;
import io.zhc1.fixedlength4j.annotation.Pad;

class StringConverterTest {
    StringConverter sut = new StringConverter();

    static class TestEntity {
        @Fixed(bytes = 10, order = 1, pad = Pad.SPACE, align = Align.LEFT)
        private String leftAlignValue;

        @Fixed(bytes = 10, order = 2, pad = Pad.SPACE, align = Align.RIGHT)
        private String rightAlignValue;
    }

    @Test
    @DisplayName("Converts String to string representation (padding applied by FixedLengthFormat)")
    void test_convertToString_left_align() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("leftAlignValue");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        String actual = sut.convertToString("john", annotation);

        // then
        // Converter returns raw value without padding; padding is applied by FixedLengthFormat
        assertEquals("john", actual);
    }

    @Test
    @DisplayName("Converts String to string representation regardless of alignment")
    void test_convertToString_right_align() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("rightAlignValue");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        String actual = sut.convertToString("john", annotation);

        // then
        // Converter returns raw value without padding; padding is applied by FixedLengthFormat
        assertEquals("john", actual);
    }

    @Test
    @DisplayName("Converts left-aligned fixed-length string to String")
    void test_convertToObject_left_align() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("leftAlignValue");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        String actual = sut.convertToObject("john      ", annotation);

        // then
        assertEquals("john", actual);
    }

    @Test
    @DisplayName("Converts right-aligned fixed-length string to String")
    void test_convertToObject_right_align() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("rightAlignValue");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        String actual = sut.convertToObject("      john", annotation);

        // then
        assertEquals("john", actual);
    }
}
