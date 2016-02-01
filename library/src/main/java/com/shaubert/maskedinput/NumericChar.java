package com.shaubert.maskedinput;

import android.content.Context;
import android.text.InputType;
import android.text.style.ForegroundColorSpan;

public class NumericChar implements MaskChar {

    private Integer maskColor;
    private Integer valueColor;

    public NumericChar() {
        this(null, null);
    }

    public NumericChar(Context context) {
        this(Util.getSecondaryColor(context), null);
    }

    public NumericChar(Integer maskColor, Integer valueColor) {
        this.maskColor = maskColor;
        this.valueColor = valueColor;
    }

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