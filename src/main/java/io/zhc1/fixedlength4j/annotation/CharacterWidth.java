package io.zhc1.fixedlength4j.annotation;

/**
 * Character width conversion strategy for half-width and full-width characters.
 * Used primarily for legacy system integration and display formatting.
 */
public enum CharacterWidth {
    /** Preserve original character width (default) */
    PRESERVE,

    /** Convert to half-width characters */
    HALF,

    /** Convert to full-width characters */
    FULL,

    /** Half-width only - throws exception if full-width characters are present */
    HALF_ONLY,

    /** Full-width only - throws exception if half-width characters are present */
    FULL_ONLY
}
