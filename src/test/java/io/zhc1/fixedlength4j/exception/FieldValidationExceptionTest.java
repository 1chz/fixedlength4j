package io.zhc1.fixedlength4j.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FieldValidationExceptionTest {

    @Test
    @DisplayName("FieldValidationException should be a subclass of FixedLengthException")
    void test_inheritance() {
        // given
        FieldValidationException exception = new FieldValidationException("Test");

        // then
        assertThat(exception).isInstanceOf(FixedLengthException.class);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("FieldValidationException with simple message")
    void test_simpleMessage() {
        // given
        String message = "Field value exceeds maximum bytes";

        // when
        FieldValidationException exception = new FieldValidationException(message);

        // then
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("FieldValidationException with cause preserves original exception")
    void test_withCause() {
        // given
        String message = "Validation failed";
        IllegalStateException cause = new IllegalStateException("Invalid state");

        // when
        FieldValidationException exception = new FieldValidationException(message, cause);

        // then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("FieldValidationException with field context provides detailed error info")
    void test_withFieldContext() {
        // given
        String message = "Value exceeds maximum bytes. Actual: 15, Max: 10";
        String fieldName = "customerName";
        Class<?> fieldType = String.class;
        int fieldOrder = 3;
        int bytePosition = 30;

        // when
        FieldValidationException exception =
                new FieldValidationException(message, fieldName, fieldType, fieldOrder, bytePosition);

        // then
        assertThat(exception.getMessage()).contains("customerName");
        assertThat(exception.getMessage()).contains("String");
        assertThat(exception.getMessage()).contains("Order: 3");
        assertThat(exception.getMessage()).contains("Position: 30");
        assertThat(exception.getFieldName()).isEqualTo("customerName");
        assertThat(exception.getFieldType()).isEqualTo(String.class);
    }

    @Test
    @DisplayName("FieldValidationException with all parameters preserves context and cause")
    void test_fullConstructor() {
        // given
        String message = "Null value not allowed";
        String fieldName = "accountNumber";
        Class<?> fieldType = String.class;
        int fieldOrder = 1;
        int bytePosition = 0;
        NullPointerException cause = new NullPointerException();

        // when
        FieldValidationException exception =
                new FieldValidationException(message, fieldName, fieldType, fieldOrder, bytePosition, cause);

        // then
        assertThat(exception.getFieldName()).isEqualTo("accountNumber");
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getMessage()).contains("accountNumber");
    }
}
