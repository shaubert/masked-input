package com.shaubert.maskedinput.sample;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.shaubert.maskedinput.MaskedEditText;
import com.shaubert.maskedinput.metextension.MaskedMETEditText;

public class SampleActivity extends Activity {

    private EditText mask;
    private MaskedEditText maskedEditText;
    private MaskedMETEditText maskedMETEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample);

        mask = (EditText) findViewById(R.id.mask_input);
        maskedEditText = (MaskedEditText) findViewById(R.id.masked_input);
        maskedMETEditText = (MaskedMETEditText) findViewById(R.id.masked_met_input);
        if (savedInstanceState != null) {
            String mask = savedInstanceState.getString("mask");
            if (!TextUtils.isEmpty(mask)) {
                maskedEditText.setMask(mask);
                maskedMETEditText.setMask(mask);
            }
        }

        findViewById(R.id.submit_mask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitMask();
            }
        });
    }

    private void submitMask() {
        maskedEditText.setMask(mask.getText().toString());
        maskedMETEditText.setMask(mask.getText().toString());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mask", maskedEditText.getMask());
    }
}
