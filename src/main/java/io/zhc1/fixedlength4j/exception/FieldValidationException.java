package io.zhc1.fixedlength4j.exception;

/**
 * Exception thrown when a field value fails validation rules.
 *
 * <p>This exception is thrown when field data violates constraints defined
 * in the annotation or other validation rules.
 *
 * <p>Example scenarios:
 * <ul>
 *   <li>Field value exceeds maximum byte length</li>
 *   <li>Non-nullable field receives null value</li>
 *   <li>Field value contains invalid characters for the target encoding</li>
 *   <li>Duplicate order values in field definitions</li>
 * </ul>
 *
 * @see FixedLengthException
 */
public class FieldValidationException extends FixedLengthException {

    /**
     * Constructs a new FieldValidationException with the specified message.
     *
     * @param message the detail message
     */
    public FieldValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new FieldValidationException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public FieldValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new FieldValidationException with field context information.
     *
     * @param message the detail message
     * @param fieldName the name of the field that failed validation
     * @param fieldType the type of the field
     * @param fieldOrder the order of the field in the fixed-length format
     * @param bytePosition the byte position within the record
     */
    public FieldValidationException(
            String message, String fieldName, Class<?> fieldType, int fieldOrder, int bytePosition) {
        super(message, fieldName, fieldType, fieldOrder, bytePosition);
    }

    /**
     * Constructs a new FieldValidationException with field context information and cause.
     *
     * @param message the detail message
     * @param fieldName the name of the field that failed validation
     * @param fieldType the type of the field
     * @param fieldOrder the order of the field in the fixed-length format
     * @param bytePosition the byte position within the record
     * @param cause the cause of this exception
     */
    public FieldValidationException(
            String message, String fieldName, Class<?> fieldType, int fieldOrder, int bytePosition, Throwable cause) {
        super(message, fieldName, fieldType, fieldOrder, bytePosition, cause);
    }
}
