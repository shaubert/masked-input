package com.shaubert.maskedinput;

public interface MaskedInputView {
    void addMaskChar(MaskChar... maskChars);

    void clearMaskChars();

    void removeMaskChar(MaskChar maskChar);

    void removeMaskChar(char ch);

    MaskChar[] getMaskChars();

    char getPlaceholder();

    void setPlaceholder(char placeholder);

    void setMask(String mask);

    String getMask();

    String getTextFromMask();

    void setTextInMask(String text);

    boolean isMaskFilled();

    int getMaskedCharsCount();
}
