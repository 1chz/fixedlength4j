package io.zhc1.fixedlength4j.exception;

/**
 * Exception thrown when character encoding/decoding operations fail.
 *
 * <p>This exception is thrown when the library encounters issues related to
 * character set encoding or decoding during serialization/deserialization.
 *
 * <p>Example scenarios:
 * <ul>
 *   <li>Character cannot be encoded in the specified charset (e.g., Korean character in ASCII)</li>
 *   <li>Byte sequence cannot be decoded with the specified charset</li>
 *   <li>Multi-byte character boundary violation during truncation</li>
 *   <li>Unsupported or unknown charset name</li>
 * </ul>
 *
 * @see FixedLengthException
 */
public class EncodingException extends FixedLengthException {

    /**
     * Constructs a new EncodingException with the specified message.
     *
     * @param message the detail message
     */
    public EncodingException(String message) {
        super(message);
    }

    /**
     * Constructs a new EncodingException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public EncodingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new EncodingException with field context information.
     *
     * @param message the detail message
     * @param fieldName the name of the field where the encoding error occurred
     * @param fieldType the type of the field
     * @param fieldOrder the order of the field in the fixed-length format
     * @param bytePosition the byte position within the record
     */
    public EncodingException(String message, String fieldName, Class<?> fieldType, int fieldOrder, int bytePosition) {
        super(message, fieldName, fieldType, fieldOrder, bytePosition);
    }

    /**
     * Constructs a new EncodingException with field context information and cause.
     *
     * @param message the detail message
     * @param fieldName the name of the field where the encoding error occurred
     * @param fieldType the type of the field
     * @param fieldOrder the order of the field in the fixed-length format
     * @param bytePosition the byte position within the record
     * @param cause the cause of this exception
     */
    public EncodingException(
            String message, String fieldName, Class<?> fieldType, int fieldOrder, int bytePosition, Throwable cause) {
        super(message, fieldName, fieldType, fieldOrder, bytePosition, cause);
    }
}
