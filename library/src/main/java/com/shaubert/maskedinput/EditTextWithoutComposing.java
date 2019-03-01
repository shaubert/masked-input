package com.shaubert.maskedinput;

import android.content.Context;
import androidx.appcompat.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.inputmethod.*;

public class EditTextWithoutComposing extends AppCompatEditText {
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
            return new InputConnectionWithoutComposing(target, false);
        } else {
            return null;
        }
    }

}
