package com.shaubert.maskedinput;

import android.content.Context;
import android.os.Build;
import android.util.TypedValue;

class Util {

    @SuppressWarnings("deprecation")
    static int getSecondaryColor(Context context) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.textColorHint, value, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(value.resourceId);
        } else {
            return context.getResources().getColor(value.resourceId);
        }
    }

}
