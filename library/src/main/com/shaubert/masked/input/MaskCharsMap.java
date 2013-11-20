package com.shaubert.masked.input;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MaskCharsMap {
    private Map<Character, MaskChar> maskCharMap = new HashMap<Character, MaskChar>();

    public MaskCharsMap(MaskChar... maskChars) {
        for (MaskChar maskChar : maskChars) {
            maskCharMap.put(maskChar.getMaskChar(), maskChar);
        }
    }

    public boolean isMaskChar(char ch) {
        return maskCharMap.containsKey(ch);
    }

    public MaskChar getMaskChar(char ch) {
        return maskCharMap.get(ch);
    }

    public void add(MaskChar maskChar) {
        maskCharMap.put(maskChar.getMaskChar(), maskChar);
    }

    public MaskChar remove(char ch) {
        return maskCharMap.remove(ch);
    }

    public MaskChar[] getMaskChars() {
        Collection<MaskChar> values = maskCharMap.values();
        return values.toArray(new MaskChar[values.size()]);
    }
}