package io.zhc1.fixedlength4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.zhc1.fixedlength4j.exception.FieldConversionException;
import io.zhc1.fixedlength4j.fixture.NullableTestEntity;

class NullSafetyTest {
    FixedLengthFormat<NullableTestEntity> sut = new FixedLengthFormat<>(NullableTestEntity.class);

    @Test
    @DisplayName("Throws exception when serializing null value for non-nullable field")
    void test_serialize_nullable_false_with_null_value_throws_exception() {
        // given
        NullableTestEntity entity = new NullableTestEntity();
        entity.setNonNullableField(null); // nullable=false
        entity.setNullableField("test");
        entity.setNullableNumber(123);

        // when & then
        assertThatThrownBy(() -> sut.serialize(entity))
                .isInstanceOf(FieldConversionException.class)
                .hasMessageContaining("not nullable")
                .hasMessageContaining("null");
    }

    @Test
    @DisplayName("Serializes non-nullable field with valid value successfully")
    void test_serialize_nullable_false_with_valid_value_succeeds() {
        // given
        NullableTestEntity entity = new NullableTestEntity();
        entity.setNonNullableField("VALID");
        entity.setNullableField("test");
        entity.setNullableNumber(123);

        // when
        ByteBuffer actual = sut.serialize(entity);

        // then
        assertThat(actual).isNotNull();
        String result = new String(actual.array());
        assertThat(result.substring(0, 10).trim()).isEqualTo("VALID");
    }

    @Test
    @DisplayName("Uses nullValue when serializing null for nullable field")
    void test_serialize_nullable_true_with_null_value_uses_nullValue() {
        // given
        NullableTestEntity entity = new NullableTestEntity();
        entity.setNonNullableField("VALID");
        entity.setNullableField(null); // nullable=true, nullValue="N/A"
        entity.setNullableNumber(null); // nullable=true, nullValue="00000"

        // when
        ByteBuffer actual = sut.serialize(entity);

        // then
        String result = new String(actual.array());
        assertThat(result.substring(10, 20).trim()).isEqualTo("N/A");
        assertThat(result.substring(20, 25)).isEqualTo("00000");
    }

    @Test
    @DisplayName("Serializes nullable field with valid value successfully")
    void test_serialize_nullable_true_with_valid_value_succeeds() {
        // given
        NullableTestEntity entity = new NullableTestEntity();
        entity.setNonNullableField("VALID");
        entity.setNullableField("DATA");
        entity.setNullableNumber(999);

        // when
        ByteBuffer actual = sut.serialize(entity);

        // then
        String result = new String(actual.array());
        assertThat(result.substring(10, 20).trim()).isEqualTo("DATA");
        assertThat(result.substring(20, 25)).isEqualTo("00999");
    }

    @Test
    @DisplayName("Converts nullValue to null when deserializing nullable field")
    void test_deserialize_nullValue_converts_to_null() {
        // given
        String data = "VALID     N/A       00000";
        ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());

        // when
        NullableTestEntity actual = sut.deserialize(buffer).get(0);

        // then
        assertThat(actual.getNonNullableField()).isEqualTo("VALID");
        assertThat(actual.getNullableField()).isNull(); // "N/A" → null
        assertThat(actual.getNullableNumber()).isNull(); // "00000" → null
    }

    @Test
    @DisplayName("Converts non-nullValue to actual value when deserializing")
    void test_deserialize_non_nullValue_converts_to_actual_value() {
        // given
        String data = "VALID     DATA      00999";
        ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());

        // when
        NullableTestEntity actual = sut.deserialize(buffer).get(0);

        // then
        assertThat(actual.getNonNullableField()).isEqualTo("VALID");
        assertThat(actual.getNullableField()).isEqualTo("DATA");
        assertThat(actual.getNullableNumber()).isEqualTo(999);
    }
}
