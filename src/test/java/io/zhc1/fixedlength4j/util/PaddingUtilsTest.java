package io.zhc1.fixedlength4j.util;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.zhc1.fixedlength4j.annotation.Align;
import io.zhc1.fixedlength4j.annotation.Fixed;
import io.zhc1.fixedlength4j.annotation.Pad;

class PaddingUtilsTest {

    static class TestEntity {
        @Fixed(bytes = 10, order = 1, pad = Pad.SPACE, align = Align.LEFT)
        private String leftSpace;

        @Fixed(bytes = 10, order = 2, pad = Pad.ZERO, align = Align.LEFT)
        private String leftZero;

        @Fixed(bytes = 10, order = 3, pad = Pad.SPACE, align = Align.RIGHT)
        private String rightSpace;

        @Fixed(bytes = 10, order = 4, pad = Pad.ZERO, align = Align.RIGHT)
        private String rightZero;

        @Fixed(bytes = 5, order = 5)
        private String smallBytes;

        @Fixed(bytes = 10, order = 6)
        private String largeBytes;
    }

    @Test
    @DisplayName("Throws exception when applyPadding receives null value")
    @SuppressWarnings("DataFlowIssue")
    void test_applyPadding_should_notnull_value() {
        assertThatThrownBy(() -> PaddingUtils.applyPadding(null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("'value' is null.");
    }

    @Test
    @DisplayName("Throws exception when applyPadding receives null annotation")
    @SuppressWarnings("DataFlowIssue")
    void test_applyPadding_should_notnull_annotation() {
        assertThatThrownBy(() -> PaddingUtils.applyPadding("", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("'annotation' is null.");
    }

    @Test
    @DisplayName("Applies left-aligned space padding to fixed-length string")
    void test_applyPadding_left_space() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("leftSpace");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        String actual = PaddingUtils.applyPadding("john", annotation);

        // then
        assertEquals("john      ", actual);
    }

    @Test
    @DisplayName("Applies left-aligned zero padding to fixed-length string")
    void test_applyPadding_left_zero() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("leftZero");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        String actual = PaddingUtils.applyPadding("john", annotation);

        // then
        assertEquals("john000000", actual);
    }

    @Test
    @DisplayName("Applies right-aligned space padding to fixed-length string")
    void test_applyPadding_right_space() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("rightSpace");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        String actual = PaddingUtils.applyPadding("john", annotation);

        // then
        assertEquals("      john", actual);
    }

    @Test
    @DisplayName("Applies right-aligned zero padding to fixed-length string")
    void test_applyPadding_right_zero() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("rightZero");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        String actual = PaddingUtils.applyPadding("john", annotation);

        // then
        assertEquals("000000john", actual);
    }

    @Test
    @DisplayName("Throws exception when value bytes exceed annotation bytes limit")
    void test_applyPadding_should_not_value_greater_then_annotation() throws Exception {
        // given
        String value = "          ";
        Field field = TestEntity.class.getDeclaredField("smallBytes");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when & then
        assertThatThrownBy(() -> PaddingUtils.applyPadding(value, annotation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "The bytes of 'value' cannot be greater than the bytes of 'annotation'. (value: 10, annotation: 5)");
    }

    @Test
    @DisplayName("Throws exception when removePadding receives null value")
    @SuppressWarnings("DataFlowIssue")
    void test_removePadding_should_notnull_value() {
        assertThatThrownBy(() -> PaddingUtils.removePadding(null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("'value' is null.");
    }

    @Test
    @DisplayName("Throws exception when removePadding receives null annotation")
    @SuppressWarnings("DataFlowIssue")
    void test_removePadding_should_notnull_annotation() {
        assertThatThrownBy(() -> PaddingUtils.removePadding("", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("'annotation' is null.");
    }

    @Test
    @DisplayName("Removes left-aligned space padding from fixed-length string")
    void test_removePadding_left_space() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("leftSpace");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        String actual = PaddingUtils.removePadding("john      ", annotation);

        // then
        assertEquals("john", actual);
    }

    @Test
    @DisplayName("Removes left-aligned zero padding from fixed-length string")
    void test_removePadding_left_zero() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("leftZero");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        String actual = PaddingUtils.removePadding("john000000", annotation);

        // then
        assertEquals("john", actual);
    }

    @Test
    @DisplayName("Removes right-aligned space padding from fixed-length string")
    void test_removePadding_right_space() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("rightSpace");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        String actual = PaddingUtils.removePadding("      john", annotation);

        // then
        assertEquals("john", actual);
    }

    @Test
    @DisplayName("Removes right-aligned zero padding from fixed-length string")
    void test_removePadding_right_zero() throws Exception {
        // given
        Field field = TestEntity.class.getDeclaredField("rightZero");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when
        String actual = PaddingUtils.removePadding("000000john", annotation);

        // then
        assertEquals("john", actual);
    }

    @Test
    @DisplayName("Throws exception when value bytes are less than annotation bytes")
    void test_removePadding_should_not_value_less_then_annotation() throws Exception {
        // given
        String value = "     ";
        Field field = TestEntity.class.getDeclaredField("largeBytes");
        Fixed annotation = field.getAnnotation(Fixed.class);

        // when & then
        assertThatThrownBy(() -> PaddingUtils.removePadding(value, annotation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "The bytes of 'value' cannot be less than the bytes of 'annotation'. (value: 5, annotation: 10)");
    }
}
