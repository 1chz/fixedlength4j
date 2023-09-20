package io.zhc1.fixedlength4j.converter;

import io.zhc1.fixedlength4j.annotation.Fixed;

/**
 * Interface for converting between Java objects and their string representation
 * in fixed-length format.
 *
 * <p>Implement this interface to add support for custom types. In most cases,
 * extending {@link AbstractConverter} is more convenient as it provides
 * null handling and padding removal.</p>
 *
 * <h2>Example Implementation:</h2>
 * <pre>{@code
 * public class MyTypeConverter extends AbstractConverter<MyType> {
 *     @Override
 *     public String convertToString(MyType value, Fixed annotation) {
 *         return value.toString();
 *     }
 *
 *     @Override
 *     public MyType convertToObject(String value, Fixed annotation) {
 *         return MyType.parse(value.trim());
 *     }
 * }
 * }</pre>
 *
 * @param <T> the type of object this converter handles
 * @see AbstractConverter
 * @see ConverterRegistrar
 */
public interface Converter<T> {
    /**
     * Converts a Java object to its string representation for serialization.
     *
     * @param value the object to convert (may be null if field is nullable)
     * @param annotation the field's {@link Fixed} annotation containing format settings
     * @return the string representation
     */
    String convertToString(T value, Fixed annotation);

    /**
     * Converts a string from fixed-length format back to a Java object.
     *
     * @param value the string value (includes padding)
     * @param annotation the field's {@link Fixed} annotation containing format settings
     * @return the converted object
     */
    T convertToObject(String value, Fixed annotation);
}
