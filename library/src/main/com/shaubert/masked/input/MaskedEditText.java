package com.shaubert.masked.input;

import android.content.Context;
import android.os.Parcelable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

public class MaskedEditText extends EditTextWithoutComposing {

    private MaskedInputFormatterTextWatcher maskedFormatter;
    private boolean maskedFormatterAttached;
    private boolean allowTextChange;
    private boolean innerCall;
    private MaskCharsMap maskCharsMap;
    private char maskCharReplacement = '_';

    private boolean pendingMaskUpdate;

    public MaskedEditText(Context context) {
        super(context);
        init();
    }

    public MaskedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaskedEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setImeOptions(getImeOptions() | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        setInputType(InputType.TYPE_CLASS_TEXT);
        maskCharsMap = new MaskCharsMap(new NumericChar());
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        if (maskedFormatterAttached && !innerCall && !maskedFormatter.isSelfChange()) {
            if (selStart == selEnd) {
                int end = maskedFormatter.getLastAllowedSelectionPosition(getText());
                if (selStart > end) {
                    selEnd = selStart = end;
                } else {
                    selEnd = selStart = maskedFormatter.getNextSelectionPosition(selStart);
                }
                innerCall = true;
                setSelection(selStart, selEnd);
                innerCall = false;
            }
        } else {
            super.onSelectionChanged(selStart, selEnd);
        }
    }

    public void addMaskChar(MaskChar maskChar) {
        maskCharsMap.add(maskChar);
        refreshMask();
    }

    public void removeMaskChar(MaskChar maskChar) {
        removeMaskChar(maskChar.getMaskChar());
    }

    public void removeMaskChar(char ch) {
        if (maskCharsMap.remove(ch) != null) {
            refreshMask();
        }
    }

    public MaskChar[] getMaskChars() {
        return maskCharsMap.getMaskChars();
    }

    public char getMaskCharReplacement() {
        return maskCharReplacement;
    }

    public void setMaskCharReplacement(char maskCharReplacement) {
        this.maskCharReplacement = maskCharReplacement;
        refreshMask();
    }

    private void refreshMask() {
        if (maskedFormatterAttached) {
            setMask(maskedFormatter.getMask());
        } else {
            pendingMaskUpdate = true;
        }
    }

    public void setMask(String mask) {
        if (!TextUtils.isEmpty(mask)) {
            pendingMaskUpdate = false;

            String oldVal = getTextFromMask();
            detachMaskedFormatter();
            maskedFormatter = new MaskedInputFormatterTextWatcher(maskCharsMap, maskCharReplacement);
            maskedFormatter.setMask(mask);
            setRawInputType(maskedFormatter.getInputType());
            safeSetText(maskedFormatter.getFormattedMask());
            attachMaskedFormatter();
            if (!TextUtils.isEmpty(oldVal)) {
                setTextInMask(oldVal);
            }
        } else {
            detachMaskedFormatter();
        }
    }

    public String getMask() {
        if (maskedFormatterAttached) {
            return maskedFormatter.getMask();
        }
        return null;
    }

    private void safeSetText(CharSequence text) {
        allowTextChange = true;
        setText(text);
    }

    private void detachMaskedFormatter() {
        if (maskedFormatterAttached) {
            maskedFormatterAttached = false;
            removeTextChangedListener(maskedFormatter);
        }
    }

    private void attachMaskedFormatter() {
        if (!maskedFormatterAttached && maskedFormatter != null) {
            maskedFormatterAttached = true;
            if (pendingMaskUpdate) {
                setMask(maskedFormatter.getMask());
            }
            addTextChangedListener(maskedFormatter);
        }
    }

    public String getTextFromMask() {
        if (maskedFormatterAttached) {
            String unmaskedValue = maskedFormatter.getUnmaskedValue(getText().toString());
            return unmaskedValue == null ? "" : unmaskedValue;
        } else {
            return "";
        }
    }

    public void setTextInMask(String text) {
        if (!maskedFormatterAttached) {
            safeSetText(text);
        } else {
            detachMaskedFormatter();
            safeSetText(maskedFormatter.getFormattedValue(text));
            attachMaskedFormatter();
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (maskedFormatterAttached && !allowTextChange) {
            throw new IllegalStateException("setting text disabled when using mask, use setTextInMask instead");
        }
        super.setText(text, type);
        allowTextChange = false;
    }

    public boolean isMaskFilled() {
        if (maskedFormatterAttached) {
            String text = getTextFromMask();
            int len = text == null ? 0 : text.length();
            return len == maskedFormatter.getMaskedCharsCount();
        }
        return false;
    }

    public int getMaskedCharsCount() {
        if (maskedFormatterAttached) {
            return maskedFormatter.getMaskedCharsCount();
        } else {
            return 0;
        }
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        detachMaskedFormatter();
        super.onRestoreInstanceState(state);
        attachMaskedFormatter();
    }
}
