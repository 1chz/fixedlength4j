package io.zhc1.fixedlength4j.util;

import io.zhc1.fixedlength4j.annotation.CharacterWidth;
import io.zhc1.fixedlength4j.exception.FieldValidationException;

/**
 * Utility class for handling half-width and full-width character conversions.
 * Supports conversion between half-width (0x0021-0x007E) and full-width (0xFF01-0xFF5E) characters.
 */
public final class CharacterWidthUtils {
    private CharacterWidthUtils() {
        throw new UnsupportedOperationException();
    }

    // Unicode offset between full-width and half-width characters
    private static final int FULLWIDTH_OFFSET = 0xFEE0;

    // Full-width space character
    private static final char FULLWIDTH_SPACE = '\u3000';

    // Half-width ASCII space character
    private static final char HALFWIDTH_SPACE = ' ';

    /**
     * Converts full-width characters to half-width characters.
     * - Full-width alphanumeric (0xFF01-0xFF5E) to half-width (0x0021-0x007E)
     * - Full-width space to half-width space
     *
     * @param str the string to convert
     * @return the string with full-width characters converted to half-width
     */
    public static String toHalfWidth(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            // Full-width alphanumeric and symbols (0xFF01-0xFF5E) -> half-width
            if (c >= 0xFF01 && c <= 0xFF5E) {
                sb.append((char) (c - FULLWIDTH_OFFSET));
            }
            // Full-width space -> half-width space
            else if (c == FULLWIDTH_SPACE) {
                sb.append(HALFWIDTH_SPACE);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Converts half-width characters to full-width characters.
     * - Half-width printable ASCII (0x0021-0x007E) to full-width (0xFF01-0xFF5E)
     * - Half-width space to full-width space
     *
     * @param str the string to convert
     * @return the string with half-width characters converted to full-width
     */
    public static String toFullWidth(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            // Half-width printable ASCII (0x0021-0x007E) -> full-width
            if (c >= 0x21 && c <= 0x7E) {
                sb.append((char) (c + FULLWIDTH_OFFSET));
            }
            // Half-width space -> full-width space
            else if (c == HALFWIDTH_SPACE) {
                sb.append(FULLWIDTH_SPACE);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Checks if the string contains any half-width ASCII characters.
     * Half-width characters include printable ASCII (0x0020-0x007E) and
     * half-width katakana (0xFF61-0xFFDC).
     *
     * @param str the string to check
     * @return true if the string contains half-width characters
     */
    public static boolean containsHalfWidth(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            // Half-width ASCII printable characters (including space)
            if (c >= 0x20 && c <= 0x7E) {
                return true;
            }
            // Half-width katakana
            if (c >= 0xFF61 && c <= 0xFFDC) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the string contains any full-width alphanumeric or symbol characters.
     * Full-width characters include 0xFF01-0xFF5E range and full-width space (0x3000).
     *
     * @param str the string to check
     * @return true if the string contains full-width characters
     */
    public static boolean containsFullWidth(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            // Full-width alphanumeric and symbols
            if (c >= 0xFF01 && c <= 0xFF5E) {
                return true;
            }
            // Full-width space
            if (c == FULLWIDTH_SPACE) {
                return true;
            }
        }
        return false;
    }

    /**
     * Applies character width processing based on the annotation settings.
     *
     * @param value the string value to process
     * @param width the character width mode
     * @param allowMixedWidth whether mixed half/full-width is allowed
     * @return the processed string
     * @throws FieldValidationException if width constraints are violated
     */
    public static String applyWidth(String value, CharacterWidth width, boolean allowMixedWidth) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        switch (width) {
            case HALF:
                return toHalfWidth(value);

            case FULL:
                return toFullWidth(value);

            case HALF_ONLY:
                if (containsFullWidth(value)) {
                    throw new FieldValidationException("Full-width characters are not allowed in HALF_ONLY mode");
                }
                return value;

            case FULL_ONLY:
                if (containsHalfWidth(value)) {
                    throw new FieldValidationException("Half-width characters are not allowed in FULL_ONLY mode");
                }
                return value;

            case PRESERVE:
            default:
                if (!allowMixedWidth) {
                    boolean hasHalf = containsHalfWidth(value);
                    boolean hasFull = containsFullWidth(value);
                    if (hasHalf && hasFull) {
                        throw new FieldValidationException(
                                "Mixed half-width and full-width characters are not allowed when allowMixedWidth=false");
                    }
                }
                return value;
        }
    }
}
