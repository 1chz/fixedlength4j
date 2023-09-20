package io.zhc1.fixedlength4j.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FixedLengthExceptionTest {

    @Test
    @DisplayName("FixedLengthException with simple message should preserve message")
    void test_simpleMessage() {
        // given
        String message = "Test error message";

        // when
        FixedLengthException exception = new FixedLengthException(message);

        // then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getFieldName()).isNull();
        assertThat(exception.getFieldType()).isNull();
        assertThat(exception.getFieldOrder()).isEqualTo(-1);
        assertThat(exception.getBytePosition()).isEqualTo(-1);
    }

    @Test
    @DisplayName("FixedLengthException with cause should preserve cause")
    void test_withCause() {
        // given
        String message = "Test error message";
        RuntimeException cause = new RuntimeException("Original cause");

        // when
        FixedLengthException exception = new FixedLengthException(message, cause);

        // then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("FixedLengthException with field context should format message correctly")
    void test_withFieldContext() {
        // given
        String message = "Value exceeds max bytes";
        String fieldName = "amount";
        Class<?> fieldType = Integer.class;
        int fieldOrder = 3;
        int bytePosition = 50;

        // when
        FixedLengthException exception =
                new FixedLengthException(message, fieldName, fieldType, fieldOrder, bytePosition);

        // then
        assertThat(exception.getMessage())
                .isEqualTo("[Field: amount, Type: Integer, Order: 3, Position: 50] Value exceeds max bytes");
        assertThat(exception.getFieldName()).isEqualTo("amount");
        assertThat(exception.getFieldType()).isEqualTo(Integer.class);
        assertThat(exception.getFieldOrder()).isEqualTo(3);
        assertThat(exception.getBytePosition()).isEqualTo(50);
    }

    @Test
    @DisplayName("FixedLengthException with field context and cause should preserve all information")
    void test_withFieldContextAndCause() {
        // given
        String message = "Conversion failed";
        String fieldName = "transDate";
        Class<?> fieldType = String.class;
        int fieldOrder = 1;
        int bytePosition = 0;
        NumberFormatException cause = new NumberFormatException("For input string: abc");

        // when
        FixedLengthException exception =
                new FixedLengthException(message, fieldName, fieldType, fieldOrder, bytePosition, cause);

        // then
        assertThat(exception.getMessage())
                .isEqualTo("[Field: transDate, Type: String, Order: 1, Position: 0] Conversion failed");
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getFieldName()).isEqualTo("transDate");
    }

    @Test
    @DisplayName("FixedLengthException with null fieldType should show 'unknown' in message")
    void test_withNullFieldType() {
        // given
        String message = "Test error";
        String fieldName = "testField";

        // when
        FixedLengthException exception = new FixedLengthException(message, fieldName, null, 1, 10);

        // then
        assertThat(exception.getMessage()).contains("Type: unknown");
    }
}
