package com.shaubert.maskedinput.metextension;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.shaubert.maskedinput.MaskChar;
import com.shaubert.maskedinput.MaskedEditTextDelegate;
import com.shaubert.maskedinput.MaskedInputView;

public class MaskedMETEditText extends MaterialEditText implements MaskedInputView {

    private MaskedEditTextDelegate delegate;

    public MaskedMETEditText(Context context) {
        super(context);
        init(null);
    }

    public MaskedMETEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MaskedMETEditText(Context context, AttributeSet attrs, int defStyle) {
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

    @Override
    public void addMaskChar(MaskChar... maskChars) {
        delegate.addMaskChar(maskChars);
    }

    @Override
    public void clearMaskChars() {
        delegate.clearMaskChars();
    }

    @Override
    public void removeMaskChar(MaskChar maskChar) {
        delegate.removeMaskChar(maskChar);
    }

    @Override
    public void removeMaskChar(char ch) {
        delegate.removeMaskChar(ch);
    }

    @Override
    public MaskChar[] getMaskChars() {
        return delegate.getMaskChars();
    }

    @Override
    public char getPlaceholder() {
        return delegate.getPlaceholder();
    }

    @Override
    public void setPlaceholder(char placeholder) {
        delegate.setPlaceholder(placeholder);
    }

    @Override
    public void setMask(String mask) {
        delegate.setMask(mask);
    }

    @Override
    public String getMask() {
        return delegate.getMask();
    }

    @Override
    public String getTextFromMask() {
        return delegate.getTextFromMask();
    }

    @Override
    public void setTextInMask(String text) {
        delegate.setTextInMask(text);
    }

    @Override
    public boolean isMaskFilled() {
        return delegate.isMaskFilled();
    }

    @Override
    public int getMaskedCharsCount() {
        return delegate.getMaskedCharsCount();
    }


}
