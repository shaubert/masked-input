package com.shaubert.maskedinput;

import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

public class InputConnectionWithoutComposing extends InputConnectionWrapper {

    private boolean enabled;

    public InputConnectionWithoutComposing(InputConnection target, boolean mutable) {
        super(target, mutable);
    }

    public void setComposingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean setComposingRegion(int start, int end) {
        return enabled && super.setComposingRegion(start, end);
    }

    @Override
    public boolean setComposingText(CharSequence text, int newCursorPosition) {
        return enabled && super.setComposingText(text, newCursorPosition);
    }

    @Override
    public boolean commitCompletion(CompletionInfo text) {
        return enabled && super.commitCompletion(text);
    }

    @Override
    public boolean commitCorrection(CorrectionInfo correctionInfo) {
        return enabled && super.commitCorrection(correctionInfo);
    }
}
