package com.example.ocrmath;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
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
    
    public Bitmap toGrayscale(Bitmap input) {
    	Bitmap grayScaled = Bitmap.createBitmap(input.getWidth(),input.getHeight(),input.getConfig());
		int avg = 0;
    	for(int i = 0; i < grayScaled.getHeight(); ++i) {
    		for(int j = 0; j < grayScaled.getWidth(); ++j) {
    			// color is really just an int
    			avg = (input.getPixel(j, i) >> 16) & 0xFF;
    			avg += (input.getPixel(j, i) >> 8) & 0xFF;
    			avg += (input.getPixel(j, i) & 0xFF);
    			avg /= 3;
    			grayScaled.setPixel(j, i, avg);
    		}
    	}
    	return grayScaled;
    }
    
    public Bitmap cropVertically(Bitmap input) {
    	int topIndex = -1;
    	int botIndex = -1;
    	int THRESH = 100;
    	
    	// Find top and bottom indices
    	for(int i = 0; i < input.getHeight(); ++i) {
    		boolean found = false;
    		// Check each row
    		for(int j = 0; j < input.getWidth(); ++j) {
    			if(input.getPixel(j, i) > THRESH)
    				found = true;
    		}
    		if(found) {
    			topIndex = i;
    			break;
    		}
    	}
    	
    	for(int i = input.getHeight(); i > 0; ++i) {
    		boolean found = false;
    		// Check each row
    		for(int j = 0; j < input.getWidth(); ++j) {
    			if(input.getPixel(j, i) > THRESH)
    				found = true;
    		}
    		if(found) {
    			botIndex = i;
    			break;
    		}
    	}
    	
    	Bitmap cropped = Bitmap.createBitmap(input.getWidth(),Math.abs(botIndex - topIndex),input.getConfig());
    	
    	for(int i = 0; i < Math.abs(botIndex - topIndex); ++i) {
    		for(int j = 0; j < input.getWidth(); ++j) {
    			cropped.setPixel(j, i, input.getPixel(topIndex+i, j));
    		}
    	}
    	
    	return cropped;
    }
    
}
