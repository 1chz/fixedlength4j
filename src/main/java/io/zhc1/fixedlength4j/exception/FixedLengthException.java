package io.zhc1.fixedlength4j.exception;

/**
 * Base exception class for fixed-length format processing errors.
 *
 * <p>This exception provides detailed context about where the error occurred,
 * including field information and byte position within the fixed-length record.
 *
 * @see FieldConversionException
 * @see FieldValidationException
 * @see EncodingException
 */
public class FixedLengthException extends RuntimeException {
    private final String fieldName;
    private final Class<?> fieldType;
    private final int fieldOrder;
    private final int bytePosition;

    /**
     * Constructs a new FixedLengthException with the specified message.
     *
     * @param message the detail message
     */
    public FixedLengthException(String message) {
        super(message);
        this.fieldName = null;
        this.fieldType = null;
        this.fieldOrder = -1;
        this.bytePosition = -1;
    }

    /**
     * Constructs a new FixedLengthException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public FixedLengthException(String message, Throwable cause) {
        super(message, cause);
        this.fieldName = null;
        this.fieldType = null;
        this.fieldOrder = -1;
        this.bytePosition = -1;
    }

    /**
     * Constructs a new FixedLengthException with field context information.
     *
     * @param message the detail message
     * @param fieldName the name of the field where the error occurred
     * @param fieldType the type of the field
     * @param fieldOrder the order of the field in the fixed-length format
     * @param bytePosition the byte position within the record where the error occurred
     */
    public FixedLengthException(
            String message, String fieldName, Class<?> fieldType, int fieldOrder, int bytePosition) {
        super(formatMessage(message, fieldName, fieldType, fieldOrder, bytePosition));
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.fieldOrder = fieldOrder;
        this.bytePosition = bytePosition;
    }

    /**
     * Constructs a new FixedLengthException with field context information and cause.
     *
     * @param message the detail message
     * @param fieldName the name of the field where the error occurred
     * @param fieldType the type of the field
     * @param fieldOrder the order of the field in the fixed-length format
     * @param bytePosition the byte position within the record where the error occurred
     * @param cause the cause of this exception
     */
    public FixedLengthException(
            String message, String fieldName, Class<?> fieldType, int fieldOrder, int bytePosition, Throwable cause) {
        super(formatMessage(message, fieldName, fieldType, fieldOrder, bytePosition), cause);
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.fieldOrder = fieldOrder;
        this.bytePosition = bytePosition;
    }

    private static String formatMessage(
            String message, String fieldName, Class<?> fieldType, int fieldOrder, int bytePosition) {
        return String.format(
                "[Field: %s, Type: %s, Order: %d, Position: %d] %s",
                fieldName,
                fieldType != null ? fieldType.getSimpleName() : "unknown",
                fieldOrder,
                bytePosition,
                message);
    }

    /**
     * Returns the name of the field where the error occurred.
     *
     * @return the field name, or null if not available
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Returns the type of the field where the error occurred.
     *
     * @return the field type, or null if not available
     */
    public Class<?> getFieldType() {
        return fieldType;
    }

    /**
     * Returns the order of the field in the fixed-length format.
     *
     * @return the field order, or -1 if not available
     */
    public int getFieldOrder() {
        return fieldOrder;
    }

    /**
     * Returns the byte position within the record where the error occurred.
     *
     * @return the byte position, or -1 if not available
     */
    public int getBytePosition() {
        return bytePosition;
    }
}
