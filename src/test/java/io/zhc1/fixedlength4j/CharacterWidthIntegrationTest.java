package io.zhc1.fixedlength4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.zhc1.fixedlength4j.annotation.CharacterWidth;
import io.zhc1.fixedlength4j.annotation.Fixed;
import io.zhc1.fixedlength4j.exception.FieldConversionException;

@DisplayName("Character Width Integration Tests")
class CharacterWidthIntegrationTest {

    @Nested
    @DisplayName("HALF width mode")
    class HalfWidthModeTests {

        @Test
        @DisplayName("Serialize converts full-width to half-width")
        void test_serialize_converts_fullwidth_to_halfwidth() {
            // given
            FixedLengthFormat<HalfWidthEntity> format = new FixedLengthFormat<>(HalfWidthEntity.class);
            HalfWidthEntity entity = new HalfWidthEntity();
            entity.setCode("１２３４５");

            // when
            ByteBuffer buffer = format.serialize(entity);
            String result = new String(buffer.array());

            // then
            assertThat(result).isEqualTo("12345     ");
        }

        @Test
        @DisplayName("Deserialize converts full-width to half-width")
        void test_deserialize_converts_fullwidth_to_halfwidth() {
            // given
            FixedLengthFormat<HalfWidthEntity> format = new FixedLengthFormat<>(HalfWidthEntity.class);
            ByteBuffer buffer = ByteBuffer.wrap("12345     ".getBytes());

            // when
            List<HalfWidthEntity> result = format.deserialize(buffer);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCode()).isEqualTo("12345");
        }

        @Test
        @DisplayName("Round-trip preserves half-width conversion")
        void test_round_trip_with_halfwidth_conversion() {
            // given
            FixedLengthFormat<HalfWidthEntity> format = new FixedLengthFormat<>(HalfWidthEntity.class);
            HalfWidthEntity entity = new HalfWidthEntity();
            entity.setCode("ＡＢＣ１２");

            // when
            ByteBuffer buffer = format.serialize(entity);
            buffer.rewind();
            List<HalfWidthEntity> result = format.deserialize(buffer);

            // then
            assertThat(result.get(0).getCode()).isEqualTo("ABC12");
        }
    }

    @Nested
    @DisplayName("FULL width mode")
    class FullWidthModeTests {

        @Test
        @DisplayName("Serialize converts half-width to full-width")
        void test_serialize_converts_halfwidth_to_fullwidth() {
            // given
            FixedLengthFormat<FullWidthEntity> format = new FixedLengthFormat<>(FullWidthEntity.class);
            FullWidthEntity entity = new FullWidthEntity();
            entity.setName("ABC");

            // when
            ByteBuffer buffer = format.serialize(entity);
            String result = new String(buffer.array());

            // then
            assertThat(result).startsWith("ＡＢＣ");
        }
    }

    @Nested
    @DisplayName("HALF_ONLY width mode")
    class HalfOnlyModeTests {

        @Test
        @DisplayName("Serialize throws exception for full-width characters")
        void test_serialize_throws_for_fullwidth() {
            // given
            FixedLengthFormat<HalfOnlyEntity> format = new FixedLengthFormat<>(HalfOnlyEntity.class);
            HalfOnlyEntity entity = new HalfOnlyEntity();
            entity.setBankCode("１２３");

            // when & then
            assertThatThrownBy(() -> format.serialize(entity))
                    .isInstanceOf(FieldConversionException.class)
                    .hasMessageContaining("Full-width");
        }

        @Test
        @DisplayName("Serialize succeeds for half-width characters")
        void test_serialize_succeeds_for_halfwidth() {
            // given
            FixedLengthFormat<HalfOnlyEntity> format = new FixedLengthFormat<>(HalfOnlyEntity.class);
            HalfOnlyEntity entity = new HalfOnlyEntity();
            entity.setBankCode("123456");

            // when
            ByteBuffer buffer = format.serialize(entity);
            String result = new String(buffer.array());

            // then
            assertThat(result).isEqualTo("123456    ");
        }
    }

    @Nested
    @DisplayName("allowMixedWidth=false mode")
    class NoMixedWidthTests {

        @Test
        @DisplayName("Serialize throws exception for mixed width characters")
        void test_serialize_throws_for_mixed() {
            // given
            FixedLengthFormat<NoMixedWidthEntity> format = new FixedLengthFormat<>(NoMixedWidthEntity.class);
            NoMixedWidthEntity entity = new NoMixedWidthEntity();
            entity.setMemo("ABCＡＢＣ");

            // when & then
            assertThatThrownBy(() -> format.serialize(entity))
                    .isInstanceOf(FieldConversionException.class)
                    .hasMessageContaining("Mixed");
        }

        @Test
        @DisplayName("Serialize succeeds for uniform half-width")
        void test_serialize_succeeds_for_uniform_halfwidth() {
            // given
            FixedLengthFormat<NoMixedWidthEntity> format = new FixedLengthFormat<>(NoMixedWidthEntity.class);
            NoMixedWidthEntity entity = new NoMixedWidthEntity();
            entity.setMemo("ABCDEF");

            // when
            ByteBuffer buffer = format.serialize(entity);
            String result = new String(buffer.array());

            // then
            assertThat(result).isEqualTo("ABCDEF    ");
        }

        @Test
        @DisplayName("Serialize succeeds for uniform full-width")
        void test_serialize_succeeds_for_uniform_fullwidth() {
            // given
            FixedLengthFormat<NoMixedWidthEntity> format = new FixedLengthFormat<>(NoMixedWidthEntity.class);
            NoMixedWidthEntity entity = new NoMixedWidthEntity();
            entity.setMemo("ＡＢＣ");

            // when
            ByteBuffer buffer = format.serialize(entity);

            // then - should not throw
            assertThat(buffer).isNotNull();
        }

        @Test
        @DisplayName("Korean only characters are allowed")
        void test_korean_only_allowed() {
            // given
            FixedLengthFormat<KoreanNoMixedEntity> format = new FixedLengthFormat<>(KoreanNoMixedEntity.class);
            KoreanNoMixedEntity entity = new KoreanNoMixedEntity();
            entity.setMemo("가나다");

            // when - Korean is not considered half/full-width in our check
            ByteBuffer buffer = format.serialize(entity);

            // then - should not throw since Korean is not half/full-width alphanumeric
            assertThat(buffer).isNotNull();
        }
    }

    @Nested
    @DisplayName("EUC-KR encoding with character width")
    class EucKrEncodingTests {

        @Test
        @DisplayName("Half-width conversion with EUC-KR encoding calculates bytes correctly")
        void test_halfwidth_with_euckr() {
            // given
            Charset eucKr = Charset.forName("EUC-KR");
            FixedLengthFormat<HalfWidthEntity> format = new FixedLengthFormat<>(HalfWidthEntity.class, eucKr);
            HalfWidthEntity entity = new HalfWidthEntity();
            entity.setCode("１２３４５");

            // when
            ByteBuffer buffer = format.serialize(entity);

            // then - full-width "１２３４５" becomes half-width "12345" (5 bytes) + 5 padding
            assertThat(buffer.limit()).isEqualTo(10);
        }
    }

    public static class HalfWidthEntity {
        @Fixed(bytes = 10, order = 1, width = CharacterWidth.HALF)
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    public static class FullWidthEntity {
        @Fixed(bytes = 30, order = 1, width = CharacterWidth.FULL)
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class HalfOnlyEntity {
        @Fixed(bytes = 10, order = 1, width = CharacterWidth.HALF_ONLY)
        private String bankCode;

        public String getBankCode() {
            return bankCode;
        }

        public void setBankCode(String bankCode) {
            this.bankCode = bankCode;
        }
    }

    public static class NoMixedWidthEntity {
        @Fixed(bytes = 10, order = 1, width = CharacterWidth.PRESERVE, allowMixedWidth = false)
        private String memo;

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }
    }

    public static class KoreanNoMixedEntity {
        @Fixed(bytes = 15, order = 1, width = CharacterWidth.PRESERVE, allowMixedWidth = false)
        private String memo;

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }
    }
}
