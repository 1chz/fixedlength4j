package io.zhc1.fixedlength4j.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.zhc1.fixedlength4j.annotation.Align;
import io.zhc1.fixedlength4j.annotation.Fixed;
import io.zhc1.fixedlength4j.annotation.Pad;

class LocalDateConverterTest {
    LocalDateConverter sut = new LocalDateConverter();

    static class TestEntity {
        @Fixed(bytes = 12, order = 1, pad = Pad.SPACE, align = Align.RIGHT, pattern = "yyyy-MM-dd")
        private LocalDate value;
    }

    @Test
    @DisplayName("Converts LocalDate to string representation (padding applied by FixedLengthFormat)")
    void test_convertToString() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("value");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        String actual = sut.convertToString(LocalDate.of(2020, 12, 31), annotation);

        // then
        // Converter returns raw value without padding; padding is applied by FixedLengthFormat
        assertEquals("2020-12-31", actual);
    }

    @Test
    @DisplayName("Converts fixed-length string to LocalDate with custom pattern")
    void test_convertToObject() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("value");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        LocalDate actual = sut.convertToObject("  2020-12-31", annotation);

        // then
        assertEquals(LocalDate.of(2020, 12, 31), actual);
    }
}
