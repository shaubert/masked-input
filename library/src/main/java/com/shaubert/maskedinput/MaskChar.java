package com.shaubert.maskedinput;

public interface MaskChar {

    char getMaskChar();

    boolean isValid(char replacement);

    int getInputTypeClass();

    Object getSpanForPlaceholder();

    Object getSpanForValue();

}