package io.zhc1.fixedlength4j.fixture;

import io.zhc1.fixedlength4j.annotation.Align;
import io.zhc1.fixedlength4j.annotation.Fixed;
import io.zhc1.fixedlength4j.annotation.Pad;

public class NullableTestEntity {
    @Fixed(bytes = 10, order = 1, nullable = false, pad = Pad.SPACE, align = Align.LEFT)
    private String nonNullableField;

    @Fixed(bytes = 10, order = 2, nullable = true, nullValue = "N/A", pad = Pad.SPACE, align = Align.LEFT)
    private String nullableField;

    @Fixed(bytes = 5, order = 3, nullable = true, nullValue = "00000", pad = Pad.ZERO, align = Align.RIGHT)
    private Integer nullableNumber;

    public String getNonNullableField() {
        return nonNullableField;
    }

    public void setNonNullableField(String nonNullableField) {
        this.nonNullableField = nonNullableField;
    }

    public String getNullableField() {
        return nullableField;
    }

    public void setNullableField(String nullableField) {
        this.nullableField = nullableField;
    }

    public Integer getNullableNumber() {
        return nullableNumber;
    }

    public void setNullableNumber(Integer nullableNumber) {
        this.nullableNumber = nullableNumber;
    }
}
