package io.zhc1.fixedlength4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.zhc1.fixedlength4j.annotation.Fixed;
import io.zhc1.fixedlength4j.converter.Converter;
import io.zhc1.fixedlength4j.converter.ConverterRegistrar;
import io.zhc1.fixedlength4j.converter.SimpleConverterRegistrar;
import io.zhc1.fixedlength4j.exception.EncodingException;
import io.zhc1.fixedlength4j.exception.FieldConversionException;
import io.zhc1.fixedlength4j.exception.FieldValidationException;
import io.zhc1.fixedlength4j.exception.FixedLengthException;
import io.zhc1.fixedlength4j.util.PaddingUtils;

/**
 * Main class for converting between Java objects and fixed-length format data.
 *
 * <p>This class provides serialization and deserialization of Java objects to/from
 * fixed-length byte format using {@link ByteBuffer}. Fields are mapped using the
 * {@link Fixed} annotation.</p>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Define a model class
 * public class Employee {
 *     @Fixed(bytes = 10, order = 1)
 *     private String name;
 *
 *     @Fixed(bytes = 3, order = 2, pad = Pad.ZERO, align = Align.RIGHT)
 *     private int age;
 *
 *     // getters and setters
 * }
 *
 * // Serialize
 * FixedLengthFormat<Employee> format = new FixedLengthFormat<>(Employee.class);
 * ByteBuffer buffer = format.serialize(employee);
 *
 * // Deserialize
 * List<Employee> employees = format.deserialize(buffer);
 * }</pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>Instances of this class are thread-safe after construction. The same instance
 * can be used to serialize/deserialize multiple objects concurrently.</p>
 *
 * @param <T> the type of objects to serialize/deserialize
 * @see Fixed
 * @see ConverterRegistrar
 */
public final class FixedLengthFormat<T> {
    private final Class<T> clazz;

    private final List<FieldMetadata> fieldMetadataList;

    private final int totalBytes;

    /**
     * Creates a new FixedLengthFormat with default settings.
     *
     * <p>Uses UTF-8 encoding and built-in converters.</p>
     *
     * @param clazz the class to serialize/deserialize
     * @throws IllegalArgumentException if clazz is null
     * @throws FieldValidationException if field annotations are invalid
     */
    public FixedLengthFormat(Class<T> clazz) {
        this(clazz, new SimpleConverterRegistrar(), StandardCharsets.UTF_8);
    }

    /**
     * Creates a new FixedLengthFormat with a specific charset.
     *
     * @param clazz the class to serialize/deserialize
     * @param charset the default charset for encoding/decoding
     * @throws IllegalArgumentException if clazz or charset is null
     * @throws FieldValidationException if field annotations are invalid
     */
    public FixedLengthFormat(Class<T> clazz, Charset charset) {
        this(clazz, new SimpleConverterRegistrar(), charset);
    }

    /**
     * Creates a new FixedLengthFormat with custom converters.
     *
     * @param clazz the class to serialize/deserialize
     * @param converterRegistrar custom converter registry
     * @throws IllegalArgumentException if clazz or converterRegistrar is null
     * @throws FieldValidationException if field annotations are invalid
     */
    public FixedLengthFormat(Class<T> clazz, ConverterRegistrar converterRegistrar) {
        this(clazz, converterRegistrar, StandardCharsets.UTF_8);
    }

    /**
     * Creates a new FixedLengthFormat with custom converters and charset.
     *
     * @param clazz the class to serialize/deserialize
     * @param converterRegistrar custom converter registry
     * @param charset the default charset for encoding/decoding
     * @throws IllegalArgumentException if any parameter is null
     * @throws FieldValidationException if field annotations are invalid
     */
    public FixedLengthFormat(Class<T> clazz, ConverterRegistrar converterRegistrar, Charset charset) {
        if (clazz == null) {
            throw new IllegalArgumentException("'clazz' must not be null");
        }
        if (converterRegistrar == null) {
            throw new IllegalArgumentException("'converterRegistrar' must not be null");
        }
        if (charset == null) {
            throw new IllegalArgumentException("'charset' must not be null");
        }

        this.clazz = clazz;
        this.fieldMetadataList = buildFieldMetadataList(clazz, converterRegistrar, charset);
        this.totalBytes =
                fieldMetadataList.stream().mapToInt(m -> m.annotation.bytes()).sum();
    }

    /**
     * Serializes a list of objects to a ByteBuffer.
     *
     * <p>Each object is serialized sequentially into the buffer. The buffer's
     * position will be at the beginning (ready for reading) after this method returns.</p>
     *
     * @param objects the list of objects to serialize
     * @return a ByteBuffer containing the serialized data
     * @throws IllegalArgumentException if objects is null
     * @throws FieldConversionException if any field fails to convert
     */
    public ByteBuffer serialize(List<T> objects) {
        if (objects == null) {
            throw new IllegalArgumentException("'objects' must not be null.");
        }

        if (objects.isEmpty()) {
            return ByteBuffer.allocate(0);
        }

        ByteBuffer result = objects.stream()
                .reduce(
                        ByteBuffer.allocate(this.totalBytes * objects.size()),
                        (buffer, object) -> buffer.put(this.serialize(object)),
                        ByteBuffer::put);
        return (ByteBuffer) result.flip();
    }

    /**
     * Serializes a single object to a ByteBuffer.
     *
     * <p>The buffer's position will be at the beginning (ready for reading)
     * after this method returns.</p>
     *
     * @param object the object to serialize
     * @return a ByteBuffer containing the serialized data
     * @throws IllegalArgumentException if object is null
     * @throws FieldConversionException if any field fails to convert
     */
    @SuppressWarnings("unchecked")
    public ByteBuffer serialize(T object) {
        if (object == null) {
            throw new IllegalArgumentException("'object' must not be null.");
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(this.totalBytes);
        int bufferPosition = 0;

        for (FieldMetadata metadata : fieldMetadataList) {
            try {
                Converter<Object> converter = (Converter<Object>) metadata.converter;
                Object rowValue = metadata.getter.invoke(object);
                String parsedValue = converter.convertToString(rowValue, metadata.annotation);

                byte[] bytes = PaddingUtils.applyPaddingBytes(parsedValue, metadata.annotation, metadata.charset);
                byteBuffer.position(bufferPosition);
                byteBuffer.put(bytes);
                bufferPosition += bytes.length;

            } catch (FixedLengthException e) {
                throw new FieldConversionException(
                        e.getMessage(),
                        metadata.field.getName(),
                        metadata.field.getType(),
                        metadata.annotation.order(),
                        bufferPosition,
                        e.getCause());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new FieldConversionException(
                        "Failed to invoke getter: " + e.getMessage(),
                        metadata.field.getName(),
                        metadata.field.getType(),
                        metadata.annotation.order(),
                        bufferPosition,
                        e);
            } catch (RuntimeException e) {
                throw new FieldConversionException(
                        "Failed to serialize field: " + e.getMessage(),
                        metadata.field.getName(),
                        metadata.field.getType(),
                        metadata.annotation.order(),
                        bufferPosition,
                        e);
            }
        }

        return (ByteBuffer) byteBuffer.flip();
    }

    /**
     * Deserializes a ByteBuffer into a list of objects.
     *
     * <p>The buffer is read from its current position. Multiple objects will be
     * deserialized if the buffer contains enough data for more than one record.</p>
     *
     * @param byteBuffer the buffer containing serialized data
     * @return a list of deserialized objects
     * @throws IllegalArgumentException if byteBuffer is null
     * @throws FieldConversionException if any field fails to convert
     */
    @SuppressWarnings("unchecked")
    public List<T> deserialize(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            throw new IllegalArgumentException("'byteBuffer' must not be null.");
        }

        int capacity = byteBuffer.limit() / this.totalBytes;
        List<T> objects = new ArrayList<>(capacity);

        for (int i = 0; i < capacity; i++) {
            T object = this.newInstance();
            int bufferPosition = i * this.totalBytes;

            for (FieldMetadata metadata : fieldMetadataList) {
                try {
                    Converter<Object> converter = (Converter<Object>) metadata.converter;
                    String rowValue = this.readBuffer(byteBuffer, metadata.annotation.bytes(), metadata.charset);
                    Object parsedValue = converter.convertToObject(rowValue, metadata.annotation);

                    metadata.setter.invoke(object, parsedValue);
                    bufferPosition += metadata.annotation.bytes();

                } catch (FixedLengthException e) {
                    throw new FieldConversionException(
                            e.getMessage(),
                            metadata.field.getName(),
                            metadata.field.getType(),
                            metadata.annotation.order(),
                            bufferPosition,
                            e.getCause());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new FieldConversionException(
                            "Failed to invoke setter: " + e.getMessage(),
                            metadata.field.getName(),
                            metadata.field.getType(),
                            metadata.annotation.order(),
                            bufferPosition,
                            e);
                } catch (RuntimeException e) {
                    throw new FieldConversionException(
                            "Failed to deserialize field: " + e.getMessage(),
                            metadata.field.getName(),
                            metadata.field.getType(),
                            metadata.annotation.order(),
                            bufferPosition,
                            e);
                }
            }

            objects.add(object);
        }

        return objects;
    }

    private List<FieldMetadata> buildFieldMetadataList(
            Class<T> clazz, ConverterRegistrar converterRegistrar, Charset defaultCharset) {
        List<Field> orderedFields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.getAnnotation(Fixed.class) != null)
                .sorted(Comparator.comparingInt(
                        field -> field.getAnnotation(Fixed.class).order()))
                .collect(Collectors.toList());

        validateFields(orderedFields, defaultCharset);

        List<FieldMetadata> metadataList = new ArrayList<>(orderedFields.size());
        for (Field field : orderedFields) {
            Fixed annotation = field.getAnnotation(Fixed.class);
            Charset fieldCharset = resolveCharset(annotation, defaultCharset);
            Method getter = findGetter(clazz, field);
            Method setter = findSetter(clazz, field);
            Converter<?> converter = converterRegistrar.getConverter(field.getType());

            metadataList.add(new FieldMetadata(field, annotation, getter, setter, converter, fieldCharset));
        }
        return metadataList;
    }

    private void validateFields(List<Field> orderedFields, Charset defaultCharset) {
        Set<Integer> orders = new HashSet<>();

        for (Field field : orderedFields) {
            Fixed annotation = field.getAnnotation(Fixed.class);
            Charset fieldCharset = resolveCharset(annotation, defaultCharset);

            if (annotation.nullable() && annotation.nullValue().isEmpty()) {
                throw new FieldValidationException(String.format(
                        "Field '%s' is nullable=true but nullValue is empty. Please provide explicit nullValue.",
                        field.getName()));
            }

            if (annotation.nullable() && annotation.nullValue().getBytes(fieldCharset).length > annotation.bytes()) {
                throw new FieldValidationException(String.format(
                        "nullValue '%s' exceeds bytes limit. Actual: %d, Max: %d",
                        annotation.nullValue(),
                        annotation.nullValue().getBytes(fieldCharset).length,
                        annotation.bytes()));
            }

            if (annotation.nullable() && field.getType().isPrimitive()) {
                throw new FieldValidationException(String.format(
                        "Primitive type '%s' cannot be nullable. Use wrapper type '%s' instead.",
                        field.getType().getSimpleName(),
                        getWrapperType(field.getType()).getSimpleName()));
            }

            if (!orders.add(annotation.order())) {
                throw new FieldValidationException(
                        String.format("Duplicate order found: order: %d", annotation.order()));
            }
        }
    }

    private Charset resolveCharset(Fixed annotation, Charset defaultCharset) {
        if (annotation.charset().isEmpty()) {
            return defaultCharset;
        }
        try {
            return Charset.forName(annotation.charset());
        } catch (Exception e) {
            throw new EncodingException(String.format("Unsupported charset: '%s'", annotation.charset()), e);
        }
    }

    private Class<?> getWrapperType(Class<?> primitiveType) {
        if (primitiveType == int.class) return Integer.class;
        if (primitiveType == long.class) return Long.class;
        if (primitiveType == float.class) return Float.class;
        if (primitiveType == double.class) return Double.class;
        if (primitiveType == boolean.class) return Boolean.class;
        if (primitiveType == byte.class) return Byte.class;
        if (primitiveType == short.class) return Short.class;
        if (primitiveType == char.class) return Character.class;
        return primitiveType;
    }

    private static Method findGetter(Class<?> clazz, Field field) {
        try {
            return clazz.getMethod("get" + field.getName().substring(0, 1).toUpperCase()
                    + field.getName().substring(1));
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("No such getter for '" + field.getName() + "'.", e);
        }
    }

    private static Method findSetter(Class<?> clazz, Field field) {
        try {
            return clazz.getMethod(
                    "set" + field.getName().substring(0, 1).toUpperCase()
                            + field.getName().substring(1),
                    field.getType());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("No such setter for '" + field.getName() + "'.", e);
        }
    }

    private T newInstance() {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("No such default constructor in '" + clazz.getName() + "'.", e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to create instance of '" + clazz.getName() + "'.", e);
        }
    }

    private String readBuffer(ByteBuffer byteBuffer, int bytes, Charset charset) {
        byte[] array = new byte[bytes];
        for (int i = 0; i < bytes; i++) {
            array[i] = byteBuffer.get();
        }
        return new String(array, charset);
    }

    /**
     * Caches field metadata to avoid repeated reflection lookups during serialization/deserialization.
     * All metadata is computed once at FixedLengthFormat construction time.
     */
    private static final class FieldMetadata {
        final Field field;
        final Fixed annotation;
        final Method getter;
        final Method setter;
        final Converter<?> converter;
        final Charset charset;

        FieldMetadata(
                Field field, Fixed annotation, Method getter, Method setter, Converter<?> converter, Charset charset) {
            this.field = field;
            this.annotation = annotation;
            this.getter = getter;
            this.setter = setter;
            this.converter = converter;
            this.charset = charset;
        }
    }
}
