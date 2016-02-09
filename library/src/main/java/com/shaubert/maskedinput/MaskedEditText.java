package com.shaubert.maskedinput;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class MaskedEditText extends AppCompatEditText {

    private MaskedEditTextDelegate delegate;

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
        delegate = new MaskedEditTextDelegate(this, attrs);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        if (delegate == null || !delegate.dispatchOnSelectionChanged(selStart, selEnd)) {
            super.onSelectionChanged(selStart, selEnd);
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (delegate == null || !delegate.dispatchSetText(text, type)) {
            super.setText(text, type);
        }
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        delegate.dispatchOnBeforeRestoreInstanceState(state);
        super.onRestoreInstanceState(state);
        delegate.dispatchOnAfterRestoreInstanceState(state);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return delegate.dispatchOnCreateInputConnection(super.onCreateInputConnection(outAttrs));
    }

    public void addMaskChar(MaskChar ... maskChars) {
        delegate.addMaskChar(maskChars);
    }

    public void clearMaskChars() {
        delegate.clearMaskChars();
    }

    public void removeMaskChar(MaskChar maskChar) {
        delegate.removeMaskChar(maskChar);
    }

    public void removeMaskChar(char ch) {
        delegate.removeMaskChar(ch);
    }

    public MaskChar[] getMaskChars() {
        return delegate.getMaskChars();
    }

    public char getPlaceholder() {
        return delegate.getPlaceholder();
    }

    public void setPlaceholder(char placeholder) {
        delegate.setPlaceholder(placeholder);
    }

    public void setMask(String mask) {
        delegate.setMask(mask);
    }

    public String getMask() {
        return delegate.getMask();
    }

    public String getTextFromMask() {
        return delegate.getTextFromMask();
    }

    public void setTextInMask(String text) {
        delegate.setTextInMask(text);
    }

    public boolean isMaskFilled() {
        return delegate.isMaskFilled();
    }

    public int getMaskedCharsCount() {
        return delegate.getMaskedCharsCount();
    }


}
