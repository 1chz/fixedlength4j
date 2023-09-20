package io.zhc1.fixedlength4j.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry for type converters used during serialization/deserialization.
 *
 * <p>This class provides built-in converters for common types and allows
 * registration of custom converters for additional types.</p>
 *
 * <h2>Built-in Converters:</h2>
 * <ul>
 *   <li>String</li>
 *   <li>boolean/Boolean</li>
 *   <li>int/Integer, long/Long, float/Float, double/Double</li>
 *   <li>BigInteger, BigDecimal</li>
 *   <li>LocalDate, LocalDateTime</li>
 * </ul>
 *
 * <h2>Custom Converters Example:</h2>
 * <pre>{@code
 * public class MyConverterRegistrar extends ConverterRegistrar {
 *     @Override
 *     protected void addConverters(Map<Class<?>, Converter<?>> converters) {
 *         converters.put(MyType.class, new MyTypeConverter());
 *     }
 * }
 *
 * // Usage
 * FixedLengthFormat<Record> format =
 *     new FixedLengthFormat<>(Record.class, new MyConverterRegistrar());
 * }</pre>
 *
 * @see Converter
 * @see SimpleConverterRegistrar
 */
public abstract class ConverterRegistrar {
    private final Map<Class<?>, Converter<?>> converters;

    public ConverterRegistrar() {
        Map<Class<?>, Converter<?>> converters = defaultConverters();
        addConverters(converters);
        this.converters = converters;
    }

    private Map<Class<?>, Converter<?>> defaultConverters() {
        Map<Class<?>, Converter<?>> converters = new HashMap<>();

        converters.put(String.class, new StringConverter());

        converters.put(boolean.class, new BooleanConverter());
        converters.put(Boolean.class, new BooleanConverter());

        converters.put(int.class, new IntegerConverter());
        converters.put(Integer.class, new IntegerConverter());

        converters.put(long.class, new LongConverter());
        converters.put(Long.class, new LongConverter());

        converters.put(float.class, new FloatConverter());
        converters.put(Float.class, new FloatConverter());

        converters.put(double.class, new DoubleConverter());
        converters.put(Double.class, new DoubleConverter());

        converters.put(BigInteger.class, new BigIntegerConverter());
        converters.put(BigDecimal.class, new BigDecimalConverter());

        converters.put(LocalDate.class, new LocalDateConverter());
        converters.put(LocalDateTime.class, new LocalDateTimeConverter());

        return converters;
    }

    /**
     * Override this method to register custom converters.
     *
     * <p>This method is called during construction after default converters
     * are registered. You can add new converters or override existing ones.</p>
     *
     * @param converters the mutable map of converters to modify
     */
    protected abstract void addConverters(Map<Class<?>, Converter<?>> converters);

    /**
     * Returns the converter for the specified type.
     *
     * @param clazz the type to get a converter for
     * @return the converter, or null if no converter is registered for the type
     */
    public final Converter<?> getConverter(Class<?> clazz) {
        return converters.get(clazz);
    }
}
