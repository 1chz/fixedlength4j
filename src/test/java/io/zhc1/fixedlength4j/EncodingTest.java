package io.zhc1.fixedlength4j;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.zhc1.fixedlength4j.annotation.Align;
import io.zhc1.fixedlength4j.annotation.Fixed;
import io.zhc1.fixedlength4j.annotation.Pad;

class EncodingTest {

    static class KoreanEntity {
        @Fixed(bytes = 10, order = 1, pad = Pad.SPACE, align = Align.LEFT)
        private String name;

        @Fixed(bytes = 5, order = 2, pad = Pad.ZERO, align = Align.RIGHT)
        private Integer amount;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }
    }

    static class EucKrEntity {
        @Fixed(bytes = 10, order = 1, pad = Pad.SPACE, align = Align.LEFT, charset = "EUC-KR")
        private String name;

        @Fixed(bytes = 5, order = 2, pad = Pad.ZERO, align = Align.RIGHT)
        private Integer amount;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }
    }

    @Test
    @DisplayName("Serializes Korean characters correctly with UTF-8 encoding")
    void test_serialize_korean_utf8() {
        // given
        FixedLengthFormat<KoreanEntity> sut = new FixedLengthFormat<>(KoreanEntity.class, StandardCharsets.UTF_8);
        KoreanEntity entity = new KoreanEntity();
        entity.setName("홍"); // "홍" is 3 bytes in UTF-8
        entity.setAmount(100);

        // when
        ByteBuffer result = sut.serialize(entity);

        // then
        assertThat(result.limit()).isEqualTo(15); // 10 + 5
        byte[] bytes = new byte[result.limit()];
        result.get(bytes);

        // "홍" (3 bytes) + 7 spaces = 10 bytes
        String name = new String(bytes, 0, 10, StandardCharsets.UTF_8);
        assertThat(name).isEqualTo("홍       ");

        String amount = new String(bytes, 10, 5, StandardCharsets.UTF_8);
        assertThat(amount).isEqualTo("00100");
    }

    @Test
    @DisplayName("Deserializes Korean characters correctly with UTF-8 encoding")
    void test_deserialize_korean_utf8() {
        // given
        FixedLengthFormat<KoreanEntity> sut = new FixedLengthFormat<>(KoreanEntity.class, StandardCharsets.UTF_8);
        // "홍" (3 bytes) + 7 spaces + "00100"
        byte[] data = "홍       00100".getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.wrap(data);

        // when
        KoreanEntity result = sut.deserialize(buffer).get(0);

        // then
        assertThat(result.getName()).isEqualTo("홍");
        assertThat(result.getAmount()).isEqualTo(100);
    }

    @Test
    @DisplayName("Serializes Korean characters correctly with EUC-KR encoding")
    void test_serialize_korean_euckr() {
        // given
        Charset euckr = Charset.forName("EUC-KR");
        FixedLengthFormat<KoreanEntity> sut = new FixedLengthFormat<>(KoreanEntity.class, euckr);
        KoreanEntity entity = new KoreanEntity();
        entity.setName("홍길동"); // "홍길동" is 6 bytes in EUC-KR (2 bytes per character)
        entity.setAmount(100);

        // when
        ByteBuffer result = sut.serialize(entity);

        // then
        assertThat(result.limit()).isEqualTo(15); // 10 + 5
        byte[] bytes = new byte[result.limit()];
        result.get(bytes);

        // "홍길동" (6 bytes) + 4 spaces = 10 bytes
        String name = new String(bytes, 0, 10, euckr);
        assertThat(name).isEqualTo("홍길동    ");

        String amount = new String(bytes, 10, 5, euckr);
        assertThat(amount).isEqualTo("00100");
    }

    @Test
    @DisplayName("Deserializes Korean characters correctly with EUC-KR encoding")
    void test_deserialize_korean_euckr() {
        // given
        Charset euckr = Charset.forName("EUC-KR");
        FixedLengthFormat<KoreanEntity> sut = new FixedLengthFormat<>(KoreanEntity.class, euckr);
        // "홍길동" (6 bytes) + 4 spaces + "00100"
        byte[] data = "홍길동    00100".getBytes(euckr);
        ByteBuffer buffer = ByteBuffer.wrap(data);

        // when
        KoreanEntity result = sut.deserialize(buffer).get(0);

        // then
        assertThat(result.getName()).isEqualTo("홍길동");
        assertThat(result.getAmount()).isEqualTo(100);
    }

    @Test
    @DisplayName("Field-level charset overrides default charset")
    void test_field_level_charset_override() {
        // given
        // Default charset is UTF-8, but field uses EUC-KR
        FixedLengthFormat<EucKrEntity> sut = new FixedLengthFormat<>(EucKrEntity.class, StandardCharsets.UTF_8);
        EucKrEntity entity = new EucKrEntity();
        entity.setName("홍길동"); // Uses EUC-KR (6 bytes) not UTF-8 (9 bytes)
        entity.setAmount(100);

        // when
        ByteBuffer result = sut.serialize(entity);

        // then
        assertThat(result.limit()).isEqualTo(15); // 10 + 5
        byte[] bytes = new byte[result.limit()];
        result.get(bytes);

        // Verify the name field uses EUC-KR encoding
        Charset euckr = Charset.forName("EUC-KR");
        String name = new String(bytes, 0, 10, euckr);
        assertThat(name).isEqualTo("홍길동    "); // 6 bytes + 4 spaces
    }

    @Test
    @DisplayName("Serializes multiple records with Korean characters")
    void test_serialize_multiple_records_korean() {
        // given
        Charset euckr = Charset.forName("EUC-KR");
        FixedLengthFormat<KoreanEntity> sut = new FixedLengthFormat<>(KoreanEntity.class, euckr);

        KoreanEntity entity1 = new KoreanEntity();
        entity1.setName("홍길동");
        entity1.setAmount(100);

        KoreanEntity entity2 = new KoreanEntity();
        entity2.setName("김철수");
        entity2.setAmount(200);

        // when
        ByteBuffer result = sut.serialize(java.util.Arrays.asList(entity1, entity2));

        // then
        assertThat(result.limit()).isEqualTo(30); // 15 * 2
    }

    @Test
    @DisplayName("Deserializes multiple records with Korean characters")
    void test_deserialize_multiple_records_korean() {
        // given
        Charset euckr = Charset.forName("EUC-KR");
        FixedLengthFormat<KoreanEntity> sut = new FixedLengthFormat<>(KoreanEntity.class, euckr);

        byte[] record1 = "홍길동    00100".getBytes(euckr);
        byte[] record2 = "김철수    00200".getBytes(euckr);
        byte[] combined = new byte[record1.length + record2.length];
        System.arraycopy(record1, 0, combined, 0, record1.length);
        System.arraycopy(record2, 0, combined, record1.length, record2.length);

        ByteBuffer buffer = ByteBuffer.wrap(combined);

        // when
        java.util.List<KoreanEntity> results = sut.deserialize(buffer);

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getName()).isEqualTo("홍길동");
        assertThat(results.get(0).getAmount()).isEqualTo(100);
        assertThat(results.get(1).getName()).isEqualTo("김철수");
        assertThat(results.get(1).getAmount()).isEqualTo(200);
    }
}
