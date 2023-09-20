package io.zhc1.fixedlength4j.converter;

import io.zhc1.fixedlength4j.annotation.Fixed;
import io.zhc1.fixedlength4j.util.CharacterWidthUtils;

final class StringConverter extends AbstractConverter<String> {
    @Override
    public String asString(String value, Fixed annotation) {
        return CharacterWidthUtils.applyWidth(value, annotation.width(), annotation.allowMixedWidth());
    }

    @Override
    public String asObject(String value, Fixed annotation) {
        return CharacterWidthUtils.applyWidth(value, annotation.width(), annotation.allowMixedWidth());
    }
}
