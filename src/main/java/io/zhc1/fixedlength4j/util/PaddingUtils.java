package io.zhc1.fixedlength4j.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import io.zhc1.fixedlength4j.annotation.Fixed;
import io.zhc1.fixedlength4j.annotation.Pad;
import io.zhc1.fixedlength4j.exception.EncodingException;
import io.zhc1.fixedlength4j.exception.FieldValidationException;

/**
 * Utility class for handling padding operations.
 */
public final class PaddingUtils {
    private PaddingUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Applies padding to the value.
     * @param value The original value to which the padding will be applied
     * @param annotation The annotation that defines the padding.
     * @return The padded value.
     */
    public static String applyPadding(String value, Fixed annotation) {
        if (value == null) {
            throw new IllegalArgumentException("'value' is null.");
        }
        if (annotation == null) {
            throw new IllegalArgumentException("'annotation' is null.");
        }
        if (value.getBytes().length > annotation.bytes()) {
            throw new IllegalArgumentException(
                    "The bytes of 'value' cannot be greater than the bytes of 'annotation'. (value: "
                            + value.getBytes().length + ", annotation: " + annotation.bytes() + ")");
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(annotation.bytes());

        if (annotation.align().isLeft()) {
            byteBuffer.put(value.getBytes());
            if (byteBuffer.remaining() > 0) {
                byteBuffer.put(annotation.pad().repeat(byteBuffer.remaining()).getBytes());
            }
            return new String(byteBuffer.array());
        }

        int remaining = annotation.bytes() - value.getBytes().length;
        if (remaining > 0) {
            byteBuffer.put(annotation.pad().repeat(remaining).getBytes());
        }
        byteBuffer.put(value.getBytes());
        return new String(byteBuffer.array());
    }

    /**
     * Removes padding from the value.
     * @param value This is the value from which I want to remove the padding.
     * @param annotation The annotation that defines the padding.
     * @return The value without padding.
     */
    public static String removePadding(String value, Fixed annotation) {
        if (value == null) {
            throw new IllegalArgumentException("'value' is null.");
        }
        if (annotation == null) {
            throw new IllegalArgumentException("'annotation' is null.");
        }
        if (value.getBytes().length < annotation.bytes()) {
            throw new IllegalArgumentException(
                    "The bytes of 'value' cannot be less than the bytes of 'annotation'. (value: "
                            + value.getBytes().length + ", annotation: " + annotation.bytes() + ")");
        }

        // If left aligned value, padding is on the right.
        if (annotation.align().isLeft()) {
            return removeRightPadding(value, annotation);
        }

        // If right aligned value, padding is on the left.
        return removeLeftPadding(value, annotation);
    }

    private static String removeLeftPadding(String value, Fixed annotation) {
        if (value.isEmpty()) {
            return value;
        }
        String firstChar = value.substring(0, 1);
        if (!annotation.pad().character.equals(firstChar)) {
            return value;
        }

        String excludeFirstChar = value.substring(1);
        return removeLeftPadding(excludeFirstChar, annotation);
    }

    private static String removeRightPadding(String value, Fixed annotation) {
        if (value.isEmpty()) {
            return value;
        }
        String lastChar = value.substring(value.length() - 1);
        if (!annotation.pad().character.equals(lastChar)) {
            return value;
        }

        String excludeLastChar = value.substring(0, value.length() - 1);
        return removeRightPadding(excludeLastChar, annotation);
    }

    /**
     * Applies padding to the value and returns as byte array with specified charset.
     * This method handles multi-byte characters correctly by working at byte level.
     *
     * @param value The original value (already converted to string by converter)
     * @param annotation The annotation that defines the padding
     * @param charset The charset to use for encoding
     * @return The padded value as byte array with exact bytes() length
     */
    public static byte[] applyPaddingBytes(String value, Fixed annotation, Charset charset) {
        if (value == null) {
            throw new FieldValidationException("'value' is null.");
        }
        if (annotation == null) {
            throw new FieldValidationException("'annotation' is null.");
        }

        byte[] valueBytes = value.getBytes(charset);
        int targetBytes = annotation.bytes();

        if (valueBytes.length > targetBytes) {
            throw new FieldValidationException(String.format(
                    "Value exceeds maximum bytes. Actual: %d, Max: %d, Value: '%s'",
                    valueBytes.length, targetBytes, truncateForDisplay(value)));
        }

        if (valueBytes.length == targetBytes) {
            return valueBytes;
        }

        byte[] result = new byte[targetBytes];
        byte padByte = getPadByte(annotation.pad(), charset);
        int paddingBytes = targetBytes - valueBytes.length;

        if (annotation.align().isLeft()) {
            System.arraycopy(valueBytes, 0, result, 0, valueBytes.length);
            Arrays.fill(result, valueBytes.length, targetBytes, padByte);
        } else {
            Arrays.fill(result, 0, paddingBytes, padByte);
            System.arraycopy(valueBytes, 0, result, paddingBytes, valueBytes.length);
        }

        return result;
    }

    private static byte getPadByte(Pad pad, Charset charset) {
        byte[] padBytes = pad.character.getBytes(charset);
        if (padBytes.length != 1) {
            throw new EncodingException(String.format(
                    "Pad character '%s' must be single byte in charset %s", pad.character, charset.name()));
        }
        return padBytes[0];
    }

    private static String truncateForDisplay(String value) {
        if (value.length() <= 20) {
            return value;
        }
        return value.substring(0, 20) + "...";
    }
}
