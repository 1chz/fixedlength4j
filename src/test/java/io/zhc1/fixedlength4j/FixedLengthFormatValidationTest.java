package io.zhc1.fixedlength4j;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.zhc1.fixedlength4j.annotation.Fixed;
import io.zhc1.fixedlength4j.exception.FieldConversionException;
import io.zhc1.fixedlength4j.exception.FieldValidationException;

class FixedLengthFormatValidationTest {

    @Test
    @DisplayName("Throws exception when nullable field has empty nullValue")
    void test_nullable_true_with_empty_nullValue_throws_exception() {
        // given
        class InvalidEntity {
            @Fixed(bytes = 10, order = 1, nullable = true, nullValue = "")
            private String field;

            public String getField() {
                return field;
            }

            public void setField(String field) {
                this.field = field;
            }
        }

        // when & then
        assertThatThrownBy(() -> new FixedLengthFormat<>(InvalidEntity.class))
                .isInstanceOf(FieldValidationException.class)
                .hasMessageContaining("nullable=true")
                .hasMessageContaining("nullValue")
                .hasMessageContaining("empty");
    }

    @Test
    @DisplayName("Throws exception when nullValue exceeds field bytes limit")
    void test_nullable_true_with_nullValue_exceeding_bytes_throws_exception() {
        // given
        class InvalidEntity {
            @Fixed(bytes = 5, order = 1, nullable = true, nullValue = "TOOLONG")
            private String field;

            public String getField() {
                return field;
            }

            public void setField(String field) {
                this.field = field;
            }
        }

        // when & then
        assertThatThrownBy(() -> new FixedLengthFormat<>(InvalidEntity.class))
                .isInstanceOf(FieldValidationException.class)
                .hasMessageContaining("nullValue")
                .hasMessageContaining("exceeds")
                .hasMessageContaining("bytes");
    }

    @Test
    @DisplayName("Throws exception when primitive type field is marked as nullable")
    void test_primitive_type_with_nullable_true_throws_exception() {
        // given
        class InvalidEntity {
            @Fixed(bytes = 5, order = 1, nullable = true, nullValue = "00000")
            private int primitiveField;

            public int getPrimitiveField() {
                return primitiveField;
            }

            public void setPrimitiveField(int primitiveField) {
                this.primitiveField = primitiveField;
            }
        }

        // when & then
        assertThatThrownBy(() -> new FixedLengthFormat<>(InvalidEntity.class))
                .isInstanceOf(FieldValidationException.class)
                .hasMessageContaining("Primitive type")
                .hasMessageContaining("cannot be nullable")
                .hasMessageContaining("Integer");
    }

    @Test
    @DisplayName("Throws exception when multiple fields have same order value")
    void test_duplicate_order_throws_exception() {
        // given
        class InvalidEntity {
            @Fixed(bytes = 10, order = 1)
            private String field1;

            @Fixed(bytes = 10, order = 1)
            private String field2;

            public String getField1() {
                return field1;
            }

            public void setField1(String field1) {
                this.field1 = field1;
            }

            public String getField2() {
                return field2;
            }

            public void setField2(String field2) {
                this.field2 = field2;
            }
        }

        // when & then
        assertThatThrownBy(() -> new FixedLengthFormat<>(InvalidEntity.class))
                .isInstanceOf(FieldValidationException.class)
                .hasMessageContaining("Duplicate order")
                .hasMessageContaining("order: 1");
    }

    @Test
    @DisplayName("Throws exception when field value exceeds maximum bytes during serialization")
    void test_serialize_value_exceeding_bytes_throws_exception() {
        // given
        FixedLengthFormat<ByteExceedEntity> format = new FixedLengthFormat<>(ByteExceedEntity.class);
        ByteExceedEntity entity = new ByteExceedEntity();
        entity.setName("ThisValueIsTooLong");

        // when & then
        assertThatThrownBy(() -> format.serialize(entity))
                .isInstanceOf(FieldConversionException.class)
                .hasMessageContaining("exceeds")
                .hasMessageContaining("Max: 10");
    }

    @Test
    @DisplayName("Throws exception when Korean value exceeds maximum bytes with EUC-KR encoding")
    void test_serialize_korean_value_exceeding_bytes_throws_exception() {
        // given
        FixedLengthFormat<KoreanByteExceedEntity> format =
                new FixedLengthFormat<>(KoreanByteExceedEntity.class, java.nio.charset.Charset.forName("EUC-KR"));
        KoreanByteExceedEntity entity = new KoreanByteExceedEntity();
        entity.setName("가나다라마바"); // 12 bytes in EUC-KR

        // when & then
        assertThatThrownBy(() -> format.serialize(entity))
                .isInstanceOf(FieldConversionException.class)
                .hasMessageContaining("exceeds");
    }

    public static class ByteExceedEntity {
        @Fixed(bytes = 10, order = 1)
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class KoreanByteExceedEntity {
        @Fixed(bytes = 10, order = 1)
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
