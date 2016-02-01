package com.shaubert.maskedinput;

import android.content.Context;
import android.text.InputType;
import android.text.style.ForegroundColorSpan;

public class TextChar implements MaskChar {

    private Integer maskColor;
    private Integer valueColor;

    public TextChar() {
        this(null, null);
    }

    public TextChar(Context context) {
        this(Util.getSecondaryColor(context), null);
    }

    public TextChar(Integer maskColor, Integer valueColor) {
        this.maskColor = maskColor;
        this.valueColor = valueColor;
    }

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

    @Override
    public Object getSpanForPlaceholder() {
        if (maskColor != null) {
            return new ForegroundColorSpan(maskColor);
        }

        return null;
    }

    @Override
    public Object getSpanForValue() {
        if (valueColor != null) {
            return new ForegroundColorSpan(valueColor);
        }

        return null;
    }

}