package com.shaubert.masked.input;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

public class EditTextWithoutComposing extends EditText {
    public EditTextWithoutComposing(Context context) {
        super(context);
    }

    public EditTextWithoutComposing(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextWithoutComposing(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new CustomInputConnection(super.onCreateInputConnection(outAttrs), false);
    }

    private static class CustomInputConnection extends InputConnectionWrapper {

        public CustomInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public CharSequence getTextAfterCursor(int n, int flags) {
            return null;
        }

        @Override
        public CharSequence getTextBeforeCursor(int n, int flags) {
            return null;
        }
    }
}
