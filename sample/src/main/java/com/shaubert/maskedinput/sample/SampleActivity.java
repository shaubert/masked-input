package com.shaubert.maskedinput.sample;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.shaubert.maskedinput.MaskedEditText;
import com.shaubert.maskedinput.MaskedInputView;
import com.shaubert.maskedinput.metextension.MaskedMETEditText;

public class SampleActivity extends Activity {

    private EditText mask;
    private MaskedInputView[] maskedInputs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample);

        maskedInputs = new MaskedInputView[] {
                (MaskedEditText) findViewById(R.id.masked_input),
                (MaskedMETEditText) findViewById(R.id.masked_met_input)
        };

        mask = (EditText) findViewById(R.id.mask_input);
        if (savedInstanceState != null) {
            String mask = savedInstanceState.getString("mask");
            if (!TextUtils.isEmpty(mask)) {
                setMask(mask);
            }
        }

        findViewById(R.id.submit_mask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMask(mask.getText().toString());
            }
        });
    }

    private void setMask(String mask) {
        for (MaskedInputView inputView : maskedInputs) {
            inputView.setMask(mask);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mask", maskedInputs[0].getMask());
    }
}
