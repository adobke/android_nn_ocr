package com.example.ocrmath;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

public class MathOcr extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_ocr);
        ImageView start = (ImageView) findViewById(R.id.baseImage);
        ImageView gray = (ImageView) findViewById(R.id.grayedImage);
        ImageView crop = (ImageView) findViewById(R.id.croppedImage);

        
        Bitmap testImg = BitmapFactory.decodeResource(getResources(), R.drawable.test1p1);
        start.setImageBitmap(testImg);
        
        Bitmap grayScale = toGrayscale(testImg);
        gray.setImageBitmap(grayScale);

        Bitmap cropped = cropVertically(grayScale);
        crop.setImageBitmap(cropped);
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
    			avg &= 0xFF;
    			avg = (avg << 16) | (avg << 8) | avg;
    			grayScaled.setPixel(j, i, avg);
    		}
    	}
    	return grayScaled;
    }
    
    public Bitmap cropVertically(Bitmap input) {
    	int topIndex = -1;
    	int botIndex = -1;
    	int THRESH = 125;
    	
    	// Find top and bottom indices
    	
    	for(int i = 0; i < input.getHeight(); ++i) {
    		boolean found = false;
    		// Check each row
    		for(int j = 0; j < input.getWidth(); ++j) {
    			if( (input.getPixel(j, i) & 0xFF ) < THRESH)
    				found = true;
    		}
    		if(found) {
    			topIndex = i;
    			break;
    		}
    	}
    	
    	for(int i = input.getHeight()-1; i >= 0; --i) {
    		boolean found = false;
    		// Check each row
    		for(int j = 0; j < input.getWidth(); ++j) {
    			if( (input.getPixel(j, i) & 0xFF) < THRESH)
    				found = true;
    		}
    		if(found) {
    			botIndex = i;
    			break;
    		}
    	}
    	

    	Log.v("ocrdebug","top: " + topIndex);
    	Log.v("ocrdebug","bot: " + botIndex);
    	
    	Bitmap cropped = Bitmap.createBitmap(input.getWidth(),Math.abs(botIndex - topIndex),input.getConfig());
    	
    	
    	for(int i = 0; i < Math.abs(botIndex - topIndex); ++i) {
        	Log.v("ocrdebug","i: " + i);
    		for(int j = 0; j < input.getWidth(); ++j) {
    			cropped.setPixel(j, i, input.getPixel(j,topIndex+i));
    		}
    	}
    	
    	return cropped;
    }
    
}
