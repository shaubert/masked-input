package com.shaubert.maskedinput;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MaskedEditTextDelegate implements MaskedInputView {

    private EditText editText;

    private WeakReference<InputConnectionWithoutComposing> inputConnectionRef;

    private MaskedInputFormatterTextWatcher maskedFormatter;
    private boolean maskedFormatterAttached;
    private boolean allowTextChangeOnce;
    private boolean innerSelectionCall;
    private MaskCharsMap maskCharsMap;
    private char placeholder = '_';
    private int initialInputType;

    private boolean pendingMaskUpdate;

    public MaskedEditTextDelegate(EditText editText) {
        this(editText, null);
    }

    public MaskedEditTextDelegate(EditText editText, AttributeSet attrs) {
        this.editText = editText;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        editText.setImeOptions(editText.getImeOptions() | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        initialInputType = editText.getInputType();
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
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

    public boolean dispatchOnSelectionChanged(int selStart, int selEnd) {
        if (maskedFormatterAttached && !innerSelectionCall && !maskedFormatter.isSelfChange()) {
            if (selStart == selEnd) {
                int end = maskedFormatter.getLastAllowedSelectionPosition(getText());
                if (selStart >= end) {
                    selEnd = selStart = end;
                } else {
                    selEnd = selStart = Math.min(end, maskedFormatter.getNextSelectionPosition(selStart));
                }
                innerSelectionCall = true;
                setSelection(selStart, selEnd);
                innerSelectionCall = false;
            }
            return true;
        } else {
            return false;
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
            if (!TextUtils.isEmpty(oldVal)) {
                safeSetText(maskedFormatter.getFormattedValue(oldVal));
            }
            attachMaskedFormatter();

            int selectionPosition = maskedFormatter.getLastAllowedSelectionPosition(getText());
            dispatchOnSelectionChanged(selectionPosition, selectionPosition);
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
            setComposingEnabled(true);
            setRawInputType(initialInputType);
        }
    }

    private void setComposingEnabled(boolean enabled) {
        InputConnectionWithoutComposing connection = inputConnectionRef != null ? inputConnectionRef.get() : null;
        if (connection != null) {
            connection.setComposingEnabled(enabled);
        }
    }

    private void attachMaskedFormatter() {
        if (!maskedFormatterAttached && maskedFormatter != null) {
            maskedFormatterAttached = true;
            if (pendingMaskUpdate) {
                setMask(maskedFormatter.getMask());
            }
            addTextChangedListener(maskedFormatter);
            setComposingEnabled(false);
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

    public boolean dispatchSetText(CharSequence text, TextView.BufferType type) {
        if (maskedFormatterAttached && !allowTextChangeOnce) {
            throw new IllegalStateException("setting text disabled when using mask, use setTextInMask instead");
        }
        allowTextChangeOnce = false;
        return false;
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

    public void dispatchOnBeforeRestoreInstanceState(Parcelable state) {
        detachMaskedFormatter();
    }

    public void dispatchOnAfterRestoreInstanceState(Parcelable state) {
        attachMaskedFormatter();
    }

    public InputConnection dispatchOnCreateInputConnection(InputConnection result) {
        if (result != null) {
            InputConnectionWithoutComposing inputConnectionWithoutComposing = new InputConnectionWithoutComposing(result, false);
            inputConnectionWithoutComposing.setComposingEnabled(!maskedFormatterAttached);
            inputConnectionRef = new WeakReference<>(inputConnectionWithoutComposing);
            return inputConnectionWithoutComposing;
        } else {
            inputConnectionRef = null;
            return null;
        }
    }

    private Context getContext() {
        return editText.getContext();
    }

    private void setSelection(int selStart, int selEnd) {
        editText.setSelection(selStart, selEnd);
    }

    private CharSequence getText() {
        return editText.getText();
    }

    private void setText(CharSequence text) {
        editText.setText(text);
    }

    private void removeTextChangedListener(MaskedInputFormatterTextWatcher maskedFormatter) {
        editText.removeTextChangedListener(maskedFormatter);
    }

    private void addTextChangedListener(MaskedInputFormatterTextWatcher maskedFormatter) {
        editText.addTextChangedListener(maskedFormatter);
    }

    private void setRawInputType(int inputType) {
        editText.setRawInputType(inputType);
    }

}
