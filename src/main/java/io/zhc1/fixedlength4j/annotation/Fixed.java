package io.zhc1.fixedlength4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify the fixed length of the field.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Fixed {
    /**
     * The length of the field.
     * @return the length of the field
     */
    int bytes();

    /**
     * The order of the field in the fixed-length format.
     * Fields are serialized and deserialized in ascending order.
     * @return the order of the field
     */
    int order();

    /**
     * Whether the field can be null.
     * If false (default), null values will throw an exception.
     * If true, null values will be replaced with nullValue.
     * @return whether the field is nullable
     */
    boolean nullable() default false;

    /**
     * The value to use when the field is null and nullable is true.
     * This value must not be empty when nullable is true.
     * The value will be padded to match bytes() length.
     * @return the null value representation
     */
    String nullValue() default "";

    /**
     * If you need a specific pattern to parse the field, please specify it.
     * @return the pattern of the field
     */
    String pattern() default "yyyyMMdd";

    /**
     * If the length of the field is less than bytes(), specifies the characters to be padded until it becomes equal to bytes().
     * @return the character to be padded
     */
    Pad pad() default Pad.SPACE;

    /**
     * In one fixed-length data, it means whether the actual data is located on the left or right.
     * If Align.LEFT, the actual data exists on the left, and the remaining space on the right is replaced with padding characters.
     * @return the alignment of the field
     */
    Align align() default Align.LEFT;

    /**
     * The character encoding to use for this field.
     * If empty (default), the charset specified in FixedLengthFormat constructor will be used.
     * Common values: "UTF-8", "EUC-KR", "MS949"
     * @return the charset name for this field
     */
    String charset() default "";

    /**
     * Character width conversion mode for half-width and full-width characters.
     * Used primarily for legacy system integration and display formatting.
     * <ul>
     *   <li>PRESERVE (default): Keep original character width</li>
     *   <li>HALF: Convert full-width to half-width (e.g., "１２３" → "123")</li>
     *   <li>FULL: Convert half-width to full-width (e.g., "ABC" → "ＡＢＣ")</li>
     *   <li>HALF_ONLY: Only allow half-width, throw exception if full-width found</li>
     *   <li>FULL_ONLY: Only allow full-width, throw exception if half-width found</li>
     * </ul>
     * @return the character width mode
     */
    CharacterWidth width() default CharacterWidth.PRESERVE;

    /**
     * Whether to allow mixed half-width and full-width characters.
     * Only applies when width is PRESERVE.
     * If false, throws exception when both half-width and full-width are found.
     * @return whether mixed width is allowed
     */
    boolean allowMixedWidth() default true;
}
