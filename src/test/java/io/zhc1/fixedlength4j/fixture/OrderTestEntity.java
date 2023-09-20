package io.zhc1.fixedlength4j.fixture;

import io.zhc1.fixedlength4j.annotation.Align;
import io.zhc1.fixedlength4j.annotation.Fixed;
import io.zhc1.fixedlength4j.annotation.Pad;

public class OrderTestEntity {
    @Fixed(bytes = 10, order = 3, pad = Pad.SPACE, align = Align.LEFT)
    private String field3;

    @Fixed(bytes = 10, order = 1, pad = Pad.SPACE, align = Align.LEFT)
    private String field1;

    @Fixed(bytes = 10, order = 2, pad = Pad.SPACE, align = Align.LEFT)
    private String field2;

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }
}
