package com.shaubert.masked.input;

import android.content.Context;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

public class MaskedEditText extends EditTextWithoutComposing {

    private MaskedInputFormatterTextWatcher maskedFormatter;
    private boolean maskedFormatterAttached;
    private boolean allowTextChange;
    private boolean innerCall;

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

    public void setMask(String mask) {
        if (!TextUtils.isEmpty(mask)) {
            String oldVal = getTextFromMask();
            detachMaskedFormatter();
            maskedFormatter = new MaskedInputFormatterTextWatcher();
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
