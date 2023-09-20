package io.zhc1.fixedlength4j.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.CharacterCodingException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EncodingExceptionTest {

    @Test
    @DisplayName("EncodingException should be a subclass of FixedLengthException")
    void test_inheritance() {
        // given
        EncodingException exception = new EncodingException("Test");

        // then
        assertThat(exception).isInstanceOf(FixedLengthException.class);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("EncodingException with simple message")
    void test_simpleMessage() {
        // given
        String message = "Character cannot be encoded in EUC-KR";

        // when
        EncodingException exception = new EncodingException(message);

        // then
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("EncodingException with cause preserves original exception")
    void test_withCause() {
        // given
        String message = "Encoding failed";
        CharacterCodingException cause = new CharacterCodingException() {};

        // when
        EncodingException exception = new EncodingException(message, cause);

        // then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("EncodingException with field context provides detailed error info")
    void test_withFieldContext() {
        // given
        String message = "Multi-byte character boundary violation";
        String fieldName = "customerName";
        Class<?> fieldType = String.class;
        int fieldOrder = 4;
        int bytePosition = 40;

        // when
        EncodingException exception = new EncodingException(message, fieldName, fieldType, fieldOrder, bytePosition);

        // then
        assertThat(exception.getMessage()).contains("customerName");
        assertThat(exception.getMessage()).contains("String");
        assertThat(exception.getMessage()).contains("Order: 4");
        assertThat(exception.getMessage()).contains("Position: 40");
        assertThat(exception.getFieldName()).isEqualTo("customerName");
        assertThat(exception.getFieldType()).isEqualTo(String.class);
    }

    @Test
    @DisplayName("EncodingException with all parameters preserves context and cause")
    void test_fullConstructor() {
        // given
        String message = "Unsupported charset";
        String fieldName = "memo";
        Class<?> fieldType = String.class;
        int fieldOrder = 5;
        int bytePosition = 100;
        IllegalArgumentException cause = new IllegalArgumentException("Unknown charset: INVALID");

        // when
        EncodingException exception =
                new EncodingException(message, fieldName, fieldType, fieldOrder, bytePosition, cause);

        // then
        assertThat(exception.getFieldName()).isEqualTo("memo");
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getMessage()).contains("memo");
    }
}
