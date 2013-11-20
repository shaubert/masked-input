package com.shaubert.masked.input.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.shaubert.masked.input.MaskedEditText;
import com.shaubert.masked.input.R;

public class SampleActivity extends Activity {

    private EditText mask;
    private MaskedEditText maskedEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample);

        mask = (EditText) findViewById(R.id.mask_input);
        maskedEditText = (MaskedEditText) findViewById(R.id.masked_input);

        findViewById(R.id.submit_mask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitMask();
            }
        });
    }

    private void submitMask() {
        maskedEditText.setMask(mask.getText().toString());
    }
}
