package com.shaubert.maskedinput;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

public class MaskedEditText extends EditTextWithoutComposing {

    private MaskedInputFormatterTextWatcher maskedFormatter;
    private boolean maskedFormatterAttached;
    private boolean allowTextChangeOnce;
    private boolean innerCall;
    private MaskCharsMap maskCharsMap;
    private char placeholder = '_';

    private boolean pendingMaskUpdate;

    public MaskedEditText(Context context) {
        super(context);
        init(null);
    }

    public MaskedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MaskedEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setImeOptions(getImeOptions() | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        setInputType(InputType.TYPE_CLASS_TEXT);
        maskCharsMap = new MaskCharsMap(
                new NumericChar(getContext()),
                new TextChar(getContext()));

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaskedEditTextStyle);
            String attrPlaceholder = a.getString(R.styleable.MaskedEditTextStyle_mi_placeholder);
            if (!TextUtils.isEmpty(attrPlaceholder)) {
                placeholder = attrPlaceholder.charAt(0);
            }
            setMask(a.getString(R.styleable.MaskedEditTextStyle_mi_mask));
            a.recycle();
        }
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

    public void addMaskChar(MaskChar ... maskChars) {
        for (MaskChar maskChar : maskChars) {
            maskCharsMap.add(maskChar);
        }
        refreshMask();
    }

    public void clearMaskChars() {
        maskCharsMap.clear();
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

    public char getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(char placeholder) {
        this.placeholder = placeholder;
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
            maskedFormatter = new MaskedInputFormatterTextWatcher(maskCharsMap, placeholder);
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
        allowTextChangeOnce = true;
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
        if (maskedFormatterAttached && !allowTextChangeOnce) {
            throw new IllegalStateException("setting text disabled when using mask, use setTextInMask instead");
        }
        super.setText(text, type);
        allowTextChangeOnce = false;
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
