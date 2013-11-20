package com.shaubert.masked.input;

public interface MaskChar {
    char getMaskChar();

    boolean isValid(char replacement);

    int getInputTypeClass();
}