package com.shaubert.masked.input;

import android.text.InputType;

public class NumericChar implements MaskChar {
    @Override
    public char getMaskChar() {
        return '#';
    }

    @Override
    public boolean isValid(char replacement) {
        return Character.isDigit(replacement);
    }

    @Override
    public int getInputTypeClass() {
        return InputType.TYPE_CLASS_NUMBER;
    }
}