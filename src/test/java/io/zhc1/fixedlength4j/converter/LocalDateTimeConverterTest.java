package io.zhc1.fixedlength4j.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.zhc1.fixedlength4j.annotation.Align;
import io.zhc1.fixedlength4j.annotation.Fixed;
import io.zhc1.fixedlength4j.annotation.Pad;

class LocalDateTimeConverterTest {
    LocalDateTimeConverter sut = new LocalDateTimeConverter();

    static class TestEntity {
        @Fixed(bytes = 25, order = 1, pad = Pad.SPACE, align = Align.RIGHT, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime value;
    }

    @Test
    @DisplayName("Converts LocalDateTime to string representation (padding applied by FixedLengthFormat)")
    void test_convertToString() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("value");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        String actual = sut.convertToString(LocalDateTime.of(2020, 12, 31, 12, 0, 0), annotation);

        // then
        // Converter returns raw value without padding; padding is applied by FixedLengthFormat
        assertEquals("2020-12-31 12:00:00", actual);
    }

    @Test
    @DisplayName("Converts fixed-length string to LocalDateTime with custom pattern")
    void test_convertToObject() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("value");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        LocalDateTime actual = sut.convertToObject("      2020-12-31 12:00:00", annotation);

        // then
        assertEquals(LocalDateTime.of(2020, 12, 31, 12, 0, 0), actual);
    }
}
