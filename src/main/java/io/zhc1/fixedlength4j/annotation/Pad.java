package io.zhc1.fixedlength4j.annotation;

import java.util.Arrays;

public enum Pad {
    ZERO("0"),
    SPACE(" "),
    ;

    public final String character;

    Pad(String character) {
        this.character = character;
    }

    /**
     * Returns a string whose length is the specified length and whose contents are filled with the specified character.
     * @param count the length of the returned string.
     * @return the string whose length is the specified length and whose contents are filled with the specified character.
     */
    public String repeat(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count is negative: " + count);
        }
        if (count == 1) {
            return this.character;
        }
        byte[] bytes = new byte[count];
        Arrays.fill(bytes, (byte) this.character.charAt(0));
        return new String(bytes);
    }
}
