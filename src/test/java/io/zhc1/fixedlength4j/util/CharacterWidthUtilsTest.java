package io.zhc1.fixedlength4j.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.zhc1.fixedlength4j.annotation.CharacterWidth;
import io.zhc1.fixedlength4j.exception.FieldValidationException;

@DisplayName("CharacterWidthUtils Tests")
class CharacterWidthUtilsTest {

    @Nested
    @DisplayName("toHalfWidth")
    class ToHalfWidthTests {

        @Test
        @DisplayName("Converts full-width alphanumeric to half-width")
        void test_toHalfWidth_converts_fullwidth_alphanumeric() {
            // given
            String fullWidth = "ＡＢＣ１２３";

            // when
            String result = CharacterWidthUtils.toHalfWidth(fullWidth);

            // then
            assertThat(result).isEqualTo("ABC123");
        }

        @Test
        @DisplayName("Converts full-width symbols to half-width")
        void test_toHalfWidth_converts_fullwidth_symbols() {
            // given
            String fullWidth = "！＠＃＄％";

            // when
            String result = CharacterWidthUtils.toHalfWidth(fullWidth);

            // then
            assertThat(result).isEqualTo("!@#$%");
        }

        @Test
        @DisplayName("Converts full-width space to half-width space")
        void test_toHalfWidth_converts_fullwidth_space() {
            // given
            String fullWidth = "Ａ　Ｂ";

            // when
            String result = CharacterWidthUtils.toHalfWidth(fullWidth);

            // then
            assertThat(result).isEqualTo("A B");
        }

        @Test
        @DisplayName("Preserves Korean characters (not half/full width)")
        void test_toHalfWidth_preserves_korean() {
            // given
            String mixed = "ＡＢＣ가나다１２３";

            // when
            String result = CharacterWidthUtils.toHalfWidth(mixed);

            // then
            assertThat(result).isEqualTo("ABC가나다123");
        }

        @Test
        @DisplayName("Returns null for null input")
        void test_toHalfWidth_returns_null_for_null() {
            assertThat(CharacterWidthUtils.toHalfWidth(null)).isNull();
        }

        @Test
        @DisplayName("Returns empty string for empty input")
        void test_toHalfWidth_returns_empty_for_empty() {
            assertThat(CharacterWidthUtils.toHalfWidth("")).isEmpty();
        }
    }

    @Nested
    @DisplayName("toFullWidth")
    class ToFullWidthTests {

        @Test
        @DisplayName("Converts half-width alphanumeric to full-width")
        void test_toFullWidth_converts_halfwidth_alphanumeric() {
            // given
            String halfWidth = "ABC123";

            // when
            String result = CharacterWidthUtils.toFullWidth(halfWidth);

            // then
            assertThat(result).isEqualTo("ＡＢＣ１２３");
        }

        @Test
        @DisplayName("Converts half-width symbols to full-width")
        void test_toFullWidth_converts_halfwidth_symbols() {
            // given
            String halfWidth = "!@#$%";

            // when
            String result = CharacterWidthUtils.toFullWidth(halfWidth);

            // then
            assertThat(result).isEqualTo("！＠＃＄％");
        }

        @Test
        @DisplayName("Converts half-width space to full-width space")
        void test_toFullWidth_converts_halfwidth_space() {
            // given
            String halfWidth = "A B";

            // when
            String result = CharacterWidthUtils.toFullWidth(halfWidth);

            // then
            assertThat(result).isEqualTo("Ａ　Ｂ");
        }

        @Test
        @DisplayName("Preserves Korean characters (not half/full width)")
        void test_toFullWidth_preserves_korean() {
            // given
            String mixed = "ABC가나다123";

            // when
            String result = CharacterWidthUtils.toFullWidth(mixed);

            // then
            assertThat(result).isEqualTo("ＡＢＣ가나다１２３");
        }

        @Test
        @DisplayName("Returns null for null input")
        void test_toFullWidth_returns_null_for_null() {
            assertThat(CharacterWidthUtils.toFullWidth(null)).isNull();
        }
    }

    @Nested
    @DisplayName("containsHalfWidth")
    class ContainsHalfWidthTests {

        @Test
        @DisplayName("Returns true for ASCII characters")
        void test_containsHalfWidth_returns_true_for_ascii() {
            assertThat(CharacterWidthUtils.containsHalfWidth("ABC")).isTrue();
            assertThat(CharacterWidthUtils.containsHalfWidth("123")).isTrue();
            assertThat(CharacterWidthUtils.containsHalfWidth("!@#")).isTrue();
        }

        @Test
        @DisplayName("Returns true for half-width space")
        void test_containsHalfWidth_returns_true_for_space() {
            assertThat(CharacterWidthUtils.containsHalfWidth("A B")).isTrue();
        }

        @Test
        @DisplayName("Returns false for full-width only")
        void test_containsHalfWidth_returns_false_for_fullwidth() {
            assertThat(CharacterWidthUtils.containsHalfWidth("ＡＢＣ")).isFalse();
            assertThat(CharacterWidthUtils.containsHalfWidth("１２３")).isFalse();
        }

        @Test
        @DisplayName("Returns false for Korean only")
        void test_containsHalfWidth_returns_false_for_korean() {
            assertThat(CharacterWidthUtils.containsHalfWidth("가나다")).isFalse();
        }

        @Test
        @DisplayName("Returns false for null or empty")
        void test_containsHalfWidth_returns_false_for_null_empty() {
            assertThat(CharacterWidthUtils.containsHalfWidth(null)).isFalse();
            assertThat(CharacterWidthUtils.containsHalfWidth("")).isFalse();
        }
    }

    @Nested
    @DisplayName("containsFullWidth")
    class ContainsFullWidthTests {

        @Test
        @DisplayName("Returns true for full-width alphanumeric")
        void test_containsFullWidth_returns_true_for_fullwidth() {
            assertThat(CharacterWidthUtils.containsFullWidth("ＡＢＣ")).isTrue();
            assertThat(CharacterWidthUtils.containsFullWidth("１２３")).isTrue();
        }

        @Test
        @DisplayName("Returns true for full-width space")
        void test_containsFullWidth_returns_true_for_fullwidth_space() {
            assertThat(CharacterWidthUtils.containsFullWidth("Ａ　Ｂ")).isTrue();
        }

        @Test
        @DisplayName("Returns false for half-width only")
        void test_containsFullWidth_returns_false_for_halfwidth() {
            assertThat(CharacterWidthUtils.containsFullWidth("ABC")).isFalse();
            assertThat(CharacterWidthUtils.containsFullWidth("123")).isFalse();
        }

        @Test
        @DisplayName("Returns false for Korean only")
        void test_containsFullWidth_returns_false_for_korean() {
            assertThat(CharacterWidthUtils.containsFullWidth("가나다")).isFalse();
        }
    }

    @Nested
    @DisplayName("applyWidth")
    class ApplyWidthTests {

        @Test
        @DisplayName("HALF mode converts full-width to half-width")
        void test_applyWidth_half_mode() {
            // given
            String value = "ＡＢＣ１２３";

            // when
            String result = CharacterWidthUtils.applyWidth(value, CharacterWidth.HALF, true);

            // then
            assertThat(result).isEqualTo("ABC123");
        }

        @Test
        @DisplayName("FULL mode converts half-width to full-width")
        void test_applyWidth_full_mode() {
            // given
            String value = "ABC123";

            // when
            String result = CharacterWidthUtils.applyWidth(value, CharacterWidth.FULL, true);

            // then
            assertThat(result).isEqualTo("ＡＢＣ１２３");
        }

        @Test
        @DisplayName("HALF_ONLY mode throws exception for full-width")
        void test_applyWidth_half_only_throws_for_fullwidth() {
            // given
            String value = "ＡＢＣ";

            // when & then
            assertThatThrownBy(() -> CharacterWidthUtils.applyWidth(value, CharacterWidth.HALF_ONLY, true))
                    .isInstanceOf(FieldValidationException.class)
                    .hasMessageContaining("Full-width");
        }

        @Test
        @DisplayName("HALF_ONLY mode allows half-width")
        void test_applyWidth_half_only_allows_halfwidth() {
            // given
            String value = "ABC123";

            // when
            String result = CharacterWidthUtils.applyWidth(value, CharacterWidth.HALF_ONLY, true);

            // then
            assertThat(result).isEqualTo("ABC123");
        }

        @Test
        @DisplayName("FULL_ONLY mode throws exception for half-width")
        void test_applyWidth_full_only_throws_for_halfwidth() {
            // given
            String value = "ABC";

            // when & then
            assertThatThrownBy(() -> CharacterWidthUtils.applyWidth(value, CharacterWidth.FULL_ONLY, true))
                    .isInstanceOf(FieldValidationException.class)
                    .hasMessageContaining("Half-width");
        }

        @Test
        @DisplayName("FULL_ONLY mode allows full-width")
        void test_applyWidth_full_only_allows_fullwidth() {
            // given
            String value = "ＡＢＣ１２３";

            // when
            String result = CharacterWidthUtils.applyWidth(value, CharacterWidth.FULL_ONLY, true);

            // then
            assertThat(result).isEqualTo("ＡＢＣ１２３");
        }

        @Test
        @DisplayName("PRESERVE mode with allowMixedWidth=false throws for mixed")
        void test_applyWidth_preserve_no_mixed_throws() {
            // given
            String value = "ABCＡＢＣ";

            // when & then
            assertThatThrownBy(() -> CharacterWidthUtils.applyWidth(value, CharacterWidth.PRESERVE, false))
                    .isInstanceOf(FieldValidationException.class)
                    .hasMessageContaining("Mixed");
        }

        @Test
        @DisplayName("PRESERVE mode with allowMixedWidth=true allows mixed")
        void test_applyWidth_preserve_mixed_allowed() {
            // given
            String value = "ABCＡＢＣ";

            // when
            String result = CharacterWidthUtils.applyWidth(value, CharacterWidth.PRESERVE, true);

            // then
            assertThat(result).isEqualTo("ABCＡＢＣ");
        }

        @Test
        @DisplayName("Returns null for null input")
        void test_applyWidth_returns_null_for_null() {
            assertThat(CharacterWidthUtils.applyWidth(null, CharacterWidth.HALF, true))
                    .isNull();
        }

        @Test
        @DisplayName("Returns empty for empty input")
        void test_applyWidth_returns_empty_for_empty() {
            assertThat(CharacterWidthUtils.applyWidth("", CharacterWidth.HALF, true))
                    .isEmpty();
        }
    }
}
