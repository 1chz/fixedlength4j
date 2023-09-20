package io.zhc1.fixedlength4j;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.ByteBuffer;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.zhc1.fixedlength4j.fixture.OrderTestEntity;

class FieldOrderingTest {
    FixedLengthFormat<OrderTestEntity> sut = new FixedLengthFormat<>(OrderTestEntity.class);

    @Test
    @DisplayName("Serializes fields in order specified by order attribute")
    void test_serialize_fields_ordered_by_order_attribute() {
        // given
        OrderTestEntity entity = new OrderTestEntity();
        entity.setField1("FIRST");
        entity.setField2("SECOND");
        entity.setField3("THIRD");

        // when
        ByteBuffer actual = sut.serialize(entity);

        // then
        String result = new String(actual.array());
        assertThat(result).isEqualTo("FIRST     SECOND    THIRD     ");
        assertThat(result.substring(0, 10).trim()).isEqualTo("FIRST");
        assertThat(result.substring(10, 20).trim()).isEqualTo("SECOND");
        assertThat(result.substring(20, 30).trim()).isEqualTo("THIRD");
    }

    @Test
    @DisplayName("Deserializes fields in order specified by order attribute")
    void test_deserialize_fields_ordered_by_order_attribute() {
        // given
        String data = "FIRST     SECOND    THIRD     ";
        ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());

        // when
        OrderTestEntity actual = sut.deserialize(buffer).get(0);

        // then
        assertThat(actual.getField1()).isEqualTo("FIRST");
        assertThat(actual.getField2()).isEqualTo("SECOND");
        assertThat(actual.getField3()).isEqualTo("THIRD");
    }

    @Test
    @DisplayName("Serializes and deserializes data correctly with ordered fields")
    void test_serialize_deserialize_roundtrip() {
        // given
        OrderTestEntity entity = new OrderTestEntity();
        entity.setField1("AAA");
        entity.setField2("BBB");
        entity.setField3("CCC");

        // when
        ByteBuffer serialized = sut.serialize(Collections.singletonList(entity));
        OrderTestEntity deserialized = sut.deserialize(serialized).get(0);

        // then
        assertThat(deserialized.getField1()).isEqualTo("AAA");
        assertThat(deserialized.getField2()).isEqualTo("BBB");
        assertThat(deserialized.getField3()).isEqualTo("CCC");
    }
}
