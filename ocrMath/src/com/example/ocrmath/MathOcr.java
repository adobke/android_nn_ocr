package com.example.ocrmath;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MathOcr extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_ocr);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_math_ocr, menu);
        return true;
    }
    
}
