package io.zhc1.fixedlength4j.exception;

/**
 * Exception thrown when a field value cannot be converted between string and object representation.
 *
 * <p>This exception is thrown during serialization when an object value cannot be converted to string,
 * or during deserialization when a string value cannot be parsed into the target type.
 *
 * <p>Example scenarios:
 * <ul>
 *   <li>Parsing "abc" as an Integer</li>
 *   <li>Parsing "2024-13-45" as a LocalDate (invalid date)</li>
 *   <li>Converting a value that doesn't match the expected pattern</li>
 * </ul>
 *
 * @see FixedLengthException
 */
public class FieldConversionException extends FixedLengthException {

    /**
     * Constructs a new FieldConversionException with the specified message.
     *
     * @param message the detail message
     */
    public FieldConversionException(String message) {
        super(message);
    }

    /**
     * Constructs a new FieldConversionException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public FieldConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new FieldConversionException with field context information.
     *
     * @param message the detail message
     * @param fieldName the name of the field where the conversion failed
     * @param fieldType the type of the field
     * @param fieldOrder the order of the field in the fixed-length format
     * @param bytePosition the byte position within the record
     */
    public FieldConversionException(
            String message, String fieldName, Class<?> fieldType, int fieldOrder, int bytePosition) {
        super(message, fieldName, fieldType, fieldOrder, bytePosition);
    }

    /**
     * Constructs a new FieldConversionException with field context information and cause.
     *
     * @param message the detail message
     * @param fieldName the name of the field where the conversion failed
     * @param fieldType the type of the field
     * @param fieldOrder the order of the field in the fixed-length format
     * @param bytePosition the byte position within the record
     * @param cause the cause of this exception
     */
    public FieldConversionException(
            String message, String fieldName, Class<?> fieldType, int fieldOrder, int bytePosition, Throwable cause) {
        super(message, fieldName, fieldType, fieldOrder, bytePosition, cause);
    }
}
