package io.zhc1.fixedlength4j.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FieldConversionExceptionTest {

    @Test
    @DisplayName("FieldConversionException should be a subclass of FixedLengthException")
    void test_inheritance() {
        // given
        FieldConversionException exception = new FieldConversionException("Test");

        // then
        assertThat(exception).isInstanceOf(FixedLengthException.class);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("FieldConversionException with simple message")
    void test_simpleMessage() {
        // given
        String message = "Failed to parse integer";

        // when
        FieldConversionException exception = new FieldConversionException(message);

        // then
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("FieldConversionException with cause preserves original exception")
    void test_withCause() {
        // given
        String message = "Conversion failed";
        NumberFormatException cause = new NumberFormatException("For input string: abc");

        // when
        FieldConversionException exception = new FieldConversionException(message, cause);

        // then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("FieldConversionException with field context provides detailed error info")
    void test_withFieldContext() {
        // given
        String message = "Cannot parse 'abc' as Integer";
        String fieldName = "quantity";
        Class<?> fieldType = Integer.class;
        int fieldOrder = 5;
        int bytePosition = 100;

        // when
        FieldConversionException exception =
                new FieldConversionException(message, fieldName, fieldType, fieldOrder, bytePosition);

        // then
        assertThat(exception.getMessage()).contains("quantity");
        assertThat(exception.getMessage()).contains("Integer");
        assertThat(exception.getMessage()).contains("Order: 5");
        assertThat(exception.getMessage()).contains("Position: 100");
        assertThat(exception.getFieldName()).isEqualTo("quantity");
        assertThat(exception.getFieldType()).isEqualTo(Integer.class);
    }

    @Test
    @DisplayName("FieldConversionException with all parameters preserves context and cause")
    void test_fullConstructor() {
        // given
        String message = "Invalid date format";
        String fieldName = "birthDate";
        Class<?> fieldType = String.class;
        int fieldOrder = 2;
        int bytePosition = 20;
        IllegalArgumentException cause = new IllegalArgumentException("Invalid pattern");

        // when
        FieldConversionException exception =
                new FieldConversionException(message, fieldName, fieldType, fieldOrder, bytePosition, cause);

        // then
        assertThat(exception.getFieldName()).isEqualTo("birthDate");
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getMessage()).contains("birthDate");
    }
}
