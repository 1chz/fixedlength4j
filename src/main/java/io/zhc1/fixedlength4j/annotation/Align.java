package io.zhc1.fixedlength4j.annotation;

/**
 * In one fixed-length data, it means whether the actual data is located on the left or right.
 * If Align.LEFT, the actual data exists on the left, and the remaining space on the right is replaced with padding characters.
 */
public enum Align {
    LEFT,
    RIGHT,
    ;

    public boolean isLeft() {
        return this == LEFT;
    }
}
