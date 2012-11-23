package com.example.ocrmath;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
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
       //ImageView cropH = (ImageView) findViewById(R.id.croppedHImage);

        
        Bitmap testImg = BitmapFactory.decodeResource(getResources(), R.drawable.test1p1);
        start.setImageBitmap(testImg);
        
        Bitmap grayScale = toGrayscale(testImg);
        gray.setImageBitmap(grayScale);
        
        Bitmap cropped= crop(grayScale);
        crop.setImageBitmap(cropped);
        
        findIndices(cropped);

//        Bitmap cropped = cropVertically(grayScale);
//        crop.setImageBitmap(cropped);
//        
//        Bitmap croppedH = cropHorizontally(cropped);
//        cropH.setImageBitmap(croppedH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_math_ocr, menu);
        return true;
    }
    
    public Bitmap toGrayscale(Bitmap input) {
//    	int width, height;
//        height = input.getHeight();
//        width = input.getWidth();    
//
//        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//        Canvas c = new Canvas(bmpGrayscale);
//        Paint paint = new Paint();
//        ColorMatrix cm = new ColorMatrix();
//        cm.setSaturation(0);
//        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
//        paint.setColorFilter(f);
//        c.drawBitmap(input, 0, 0, paint);
//        return bmpGrayscale;
   	
    	Bitmap grayScaled = Bitmap.createBitmap(input.getWidth(),input.getHeight(),input.getConfig());
		int avg = 0;
		int black = 0;
		int white = -1;
    	for(int i = 0; i < grayScaled.getHeight(); ++i) {
    		for(int j = 0; j < grayScaled.getWidth(); ++j) {
    			// color is really just an int
    			avg = (input.getPixel(j, i) >> 16) & 0xFF;
    			avg += (input.getPixel(j, i) >> 8) & 0xFF;
    			avg += (input.getPixel(j, i) & 0xFF);
    			avg /= 3;
    			avg &= 0xFF;
    			avg = (avg << 16) | (avg << 8) | avg;
    			if ((avg & 0xFF) < 155)
    				grayScaled.setPixel(j, i, black);
    			else
    				grayScaled.setPixel(j, i, white);
    		}
    	}
    	return grayScaled;
    }
    
    public Bitmap cropVertically(Bitmap input) {
    	int topIndex = -1;
    	int botIndex = -1;
    	int THRESH = 135;
    	
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
    		for(int j = 0; j < input.getWidth(); ++j) {
    			cropped.setPixel(j, i, input.getPixel(j,topIndex+i));
    		}
    	}
    	
    	return cropped;
    }
    
    public Bitmap cropHorizontally(Bitmap input) {
    	int leftIndex = -1;
    	int rightIndex = -1;
    	int THRESH = 135;
    	
    	// Find top and bottom indices
    	
    	for(int i = 0; i < input.getWidth(); ++i) {
    		boolean found = false;
    		// Check each row
    		for(int j = 0; j < input.getHeight(); ++j) {
    			if( (input.getPixel(i, j) & 0xFF ) < THRESH)
    				found = true;
    		}
    		if(found) {
    			leftIndex = i;
    			break;
    		}
    	}
    	
    	for(int i = input.getWidth()-1; i >= 0; --i) {
    		boolean found = false;
    		// Check each row
    		for(int j = 0; j < input.getHeight(); ++j) {
    			if( (input.getPixel(i, j) & 0xFF) < THRESH)
    				found = true;
    		}
    		if(found) {
    			rightIndex = i;
    			break;
    		}
    	}
    	

    	Log.v("ocrdebug","left: " + leftIndex);
    	Log.v("ocrdebug","Right: " + rightIndex);
    	
    	Bitmap cropped = Bitmap.createBitmap(rightIndex - leftIndex,input.getHeight(),input.getConfig());
    	
    	
    	for(int i = 0; i < input.getHeight(); ++i) {
    		for(int j = 0; j < (rightIndex-leftIndex); ++j) {
    			cropped.setPixel(j, i, input.getPixel(leftIndex+j,i));
    		}
    	}
    	
    	return cropped;
    }
    
    public Bitmap crop(Bitmap input) {
    	int leftIndex = -1;
    	int rightIndex = -1;
    	int topIndex = -1;
    	int botIndex = -1;
    	int THRESH = 135;
    	  	
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
    	
    	
    	for(int i = 0; i < input.getWidth(); ++i) {
    		boolean found = false;
    		// Check each row
    		for(int j = topIndex; j < botIndex; ++j) {
    			if( (input.getPixel(i, j) & 0xFF ) < THRESH)
    				found = true;
    		}
    		if(found) {
    			leftIndex = i;
    			break;
    		}
    	}
    	
    	for(int i = input.getWidth()-1; i >= leftIndex; --i) {
    		boolean found = false;
    		// Check each row
    		for(int j = topIndex; j < botIndex; ++j) {
    			if( (input.getPixel(i, j) & 0xFF) < THRESH)
    				found = true;
    		}
    		if(found) {
    			rightIndex = i;
    			break;
    		}
    	}
    	

    	Log.v("ocrdebug","left: " + leftIndex);
    	Log.v("ocrdebug","Right: " + rightIndex);
    	
    	Bitmap cropped = Bitmap.createBitmap(rightIndex - leftIndex,Math.abs(botIndex - topIndex),input.getConfig());
    	
    	
    	for(int i = 0; i < Math.abs(botIndex - topIndex); ++i) {
    		for(int j = 0; j < (rightIndex-leftIndex); ++j) {
    			cropped.setPixel(j, i, input.getPixel(leftIndex+j,topIndex+i));
    		}
    	}
    	
    	return cropped;
    }
    
    public ArrayList < ArrayList<Integer> > findIndices(Bitmap input) {
    	int currentStart = 1;
    	int THRESH = 185;
    	ArrayList<ArrayList<Integer>> indices = new ArrayList<ArrayList<Integer>>();
    	
    	boolean foundStart = true;
    	for(int i = 1; i < input.getWidth(); ++i) {
    		boolean dark = false;
    		// Check each row
    		for(int j = 0; j < input.getHeight(); ++j) {
    			if( (input.getPixel(i, j) & 0xFF) < THRESH)
    				dark = true;
    		}
    		if(dark && !foundStart) {
    			currentStart = i;
    			foundStart = true;
    		} else if (!dark && foundStart) {
    			ArrayList<Integer> duple = new ArrayList<Integer>(2);
    			duple.add(0, currentStart-1);
    			duple.add(1, i+1);
    			indices.add(duple);
    			Log.v("ocrdebug","start: " + currentStart + " " + i);
    			foundStart = false;
    		}
    	}
    	
    	if (foundStart) {
	    	ArrayList<Integer> duple = new ArrayList<Integer>(2);
			duple.add(0, currentStart-1);
			duple.add(1, input.getWidth()-1);
			indices.add(duple);
    	}
    	
    	for(ArrayList<Integer> duple: indices) {
    		int red = (255 << 16);
    		int green = (255 << 8);
    		for(int j = 0; j < input.getHeight(); ++j) {
    			input.setPixel(duple.get(0), j, red); 
    		}
    		for(int j = 0; j < input.getHeight(); ++j) {
    			input.setPixel(duple.get(1), j, green); 
    		}
    	}
		
    	return indices;
    	
    }
    
    /* Presumably we want the images to all be the same size so that they
     * can be fed into the neural network with ease. However it can't hurt
     * to make the function generate boolean 2d arrays that can vary with
     * the input.
     */
    public ArrayList<boolean[][]> scaleBitmap(Bitmap source, int outputWidth, int outputHeight, ArrayList<ArrayList<Integer>> pairs) {
    	final int THRESH = 185;
    	final double OUTPUTTHRESH = .5;
    	int largestDim;
    	int imageWidth, imageHeight;
    	int topIndex, botIndex;
    	
    	Bitmap character;
    	
    	boolean[][] neuralInput;
    	ArrayList<boolean[][]> ret = new ArrayList<boolean[][]>(pairs.size());
    	
    	
    	// Loop over the pairs in the list.
    	for( ArrayList<Integer> pair: pairs ){
    		topIndex = 0;
    		botIndex = 0;
    	
    		// Subtract the left index from the right index to get the width
    		// of this character.
    		imageWidth = pair.get(1) - pair.get(0);
    		
    		// we want to select that area of the bitmap
    		// then we need to determine its largest dimension.
    		
    		// Determine the bottom index and top index of this part of the image,
    		// so that we can zoom in as much as possible on this part of the image.
    		for(int i = 0; i < source.getHeight(); i++) {
    			for(int j = pair.get(0); j < pair.get(1); j++){
    				if( (source.getPixel(i, j) & 0xFF) < THRESH && topIndex == 0 )
        				topIndex = i;
    				else if( (source.getPixel(i, j) & 0xFF) < THRESH)
    					botIndex = i;
    			}
    		}
    		
    		// Get the image height.
    		imageHeight = Math.abs(botIndex - topIndex);
    		
    		if(imageWidth > imageHeight)
    			largestDim = imageWidth;
    		else
    			largestDim = imageHeight;
    		
    		// now create a new largest dimension^2 Bitmap to hold our scaled image.
    		character = Bitmap.createBitmap(largestDim, largestDim, source.getConfig());
    		
    		// White out all the pixels in the new Bitmap. Doing this as a precaution.
    		for(int i = 0; i < largestDim; i++) {
    			for(int j = 0; j < largestDim; j++) {
    				character.setPixel(j, i, Color.WHITE);
    			}
    		}
    		
    		// now center the old bitmap image on the new bitmap (presumably the new image is wider, but it could be taller)
    		int startWidth = largestDim/2 - imageWidth/2;
    		int startHeight = largestDim/2 - imageHeight/2;
    		for(int i = topIndex; i < botIndex; i++) {
    			for(int j = pair.get(0); j < pair.get(1); j++){
    				if((source.getPixel(j, i) & 0xFF) < THRESH)
    					character.setPixel(startWidth + j, startHeight + i, Color.BLACK);
    			}
    		}
    		
    		// Fill in the array of booleans according to the criterion that dictates a full pixel.
    		neuralInput = new boolean[outputHeight][outputWidth];
    		
    		int count;
    		int total;
    		int width = largestDim/outputWidth;
    		int height = largestDim/outputHeight;
    		
    		for(int i = 0; i < outputHeight; i ++){
    			for(int j = 0; j < outputWidth; j++){
    				count = 0;
    				total = 0;
    				for(int imagei = i * height; imagei < (i+1) * height; imagei++) {
    					for(int imagej = j * width; imagej < (j+1) * width; imagej++){
    						if((character.getPixel(j,i) & 0xFF) < THRESH)
    							count++;
    						total++;
    					}
    				}
    				if(((double)count)/total > OUTPUTTHRESH)
    					neuralInput[i][j] = true;
    			}
    		}
    		character.recycle();
    		ret.add(neuralInput);
    	}
    	
    	return ret;
    }
}
