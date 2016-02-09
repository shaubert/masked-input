package com.shaubert.maskedinput;

import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

public class InputConnectionWithoutComposing extends InputConnectionWrapper {

    public InputConnectionWithoutComposing(InputConnection target, boolean mutable) {
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
