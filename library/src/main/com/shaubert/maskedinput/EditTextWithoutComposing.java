package com.shaubert.maskedinput;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.*;
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
        InputConnection target = super.onCreateInputConnection(outAttrs);
        if (target != null) {
            return new CustomInputConnection(target, false);
        } else {
            return null;
        }
    }

    private static class CustomInputConnection extends InputConnectionWrapper {

        public CustomInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean setComposingRegion(int start, int end) {
            return false;
        }

        @Override
        public boolean setComposingText(CharSequence text, int newCursorPosition) {
            return false;
        }

        @Override
        public boolean commitCompletion(CompletionInfo text) {
            return false;
        }

        @Override
        public boolean commitCorrection(CorrectionInfo correctionInfo) {
            return false;
        }
    }
}
