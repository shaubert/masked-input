package com.shaubert.masked.input;

import android.text.InputType;

public class TextChar implements MaskChar {
    @Override
    public char getMaskChar() {
        return '*';
    }

    @Override
    public boolean isValid(char replacement) {
        return Character.isLetter(replacement);
    }

    @Override
    public int getInputTypeClass() {
        return InputType.TYPE_CLASS_TEXT;
    }
}
