package io.zhc1.fixedlength4j.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.zhc1.fixedlength4j.annotation.Align;
import io.zhc1.fixedlength4j.annotation.Fixed;
import io.zhc1.fixedlength4j.annotation.Pad;

class BooleanConverterTest {
    BooleanConverter sut = new BooleanConverter();

    static class TestEntity {
        @Fixed(bytes = 1, order = 1)
        private Boolean booleanValue;

        @Fixed(bytes = 5, order = 2, pad = Pad.ZERO, align = Align.RIGHT)
        private Boolean paddedValue;
    }

    @Test
    @DisplayName("Converts true to '1'")
    void test_convertToString_true() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("booleanValue");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        String actual = sut.convertToString(true, annotation);

        // then
        assertThat(actual).isEqualTo("1");
    }

    @Test
    @DisplayName("Converts false to '0'")
    void test_convertToString_false() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("booleanValue");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        String actual = sut.convertToString(false, annotation);

        // then
        assertThat(actual).isEqualTo("0");
    }

    @Test
    @DisplayName("Converts '1' to true")
    void test_convertToObject_one() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("booleanValue");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        Boolean actual = sut.convertToObject("1", annotation);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("Converts '0' to false")
    void test_convertToObject_zero() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("booleanValue");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        Boolean actual = sut.convertToObject("0", annotation);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("Converts padded '00001' to true after trimming")
    void test_convertToObject_padded() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("paddedValue");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        Boolean actual = sut.convertToObject("00001", annotation);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("Throws exception for invalid boolean value")
    void test_convertToObject_invalid() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("booleanValue");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when & then
        assertThatThrownBy(() -> sut.convertToObject("true", annotation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid boolean value");
    }
}
