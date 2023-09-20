package io.zhc1.fixedlength4j.converter;

import io.zhc1.fixedlength4j.annotation.Fixed;
import io.zhc1.fixedlength4j.exception.FieldValidationException;
import io.zhc1.fixedlength4j.util.PaddingUtils;

/**
 * Abstract class for implementing {@link Converter}.
 * @param <T> Type of the value to be converted
 */
public abstract class AbstractConverter<T> implements Converter<T> {
    @Override
    public String convertToString(T value, Fixed annotation) {
        if (annotation == null) {
            throw new FieldValidationException("'annotation' must not be null.");
        }

        if (value == null) {
            if (!annotation.nullable()) {
                throw new FieldValidationException("Field is not nullable but value is null");
            }
            return annotation.nullValue();
        }

        return asString(value, annotation);
    }

    @Override
    public T convertToObject(String value, Fixed annotation) {
        if (value == null) {
            throw new FieldValidationException("'value' must not be null.");
        }
        if (annotation == null) {
            throw new FieldValidationException("'annotation' must not be null.");
        }

        String stringValue = PaddingUtils.removePadding(value, annotation);

        if (annotation.nullable()) {
            String trimmedNullValue =
                    PaddingUtils.removePadding(padNullValue(annotation.nullValue(), annotation), annotation);
            if (stringValue.equals(trimmedNullValue)) {
                return null;
            }
        }

        return asObject(stringValue, annotation);
    }

    private String padNullValue(String nullValue, Fixed annotation) {
        // Pad nullValue to full length for proper comparison after removePadding
        StringBuilder sb = new StringBuilder(nullValue);
        while (sb.length() < annotation.bytes()) {
            if (annotation.align().isLeft()) {
                sb.append(annotation.pad().character);
            } else {
                sb.insert(0, annotation.pad().character);
            }
        }
        return sb.toString();
    }

    /**
     * Convert T to String. You only need to focus on changing types safely.
     * @param value T
     * @param annotation {@link Fixed}
     * @return T converted to String
     */
    protected abstract String asString(T value, Fixed annotation);

    /**
     * Convert String to T. You only need to focus on changing types safely.
     * @param value String
     * @param annotation {@link Fixed}
     * @return String converted to T
     */
    protected abstract T asObject(String value, Fixed annotation);
}
