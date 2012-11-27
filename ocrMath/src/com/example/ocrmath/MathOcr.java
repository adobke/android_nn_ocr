package com.example.ocrmath;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

public class MathOcr extends Activity {
	//ImageView crop;
	ImageView gray;
	ImageView start;
	ImageSwitcher charImages;
	Bitmap base;
	int threshold = 140;
	
    private static final int SELECT_PICTURE = 1;

    private String selectedImagePath;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_ocr);
        
        start = (ImageView) findViewById(R.id.baseImage);
        gray = (ImageView) findViewById(R.id.grayedImage);
        //crop = (ImageView) findViewById(R.id.croppedImage);
        charImages = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        base = BitmapFactory.decodeResource(getResources(), R.drawable.test1p1);
        if(base == null){
        	Log.v("asdf", "base is null");
        }
		//setupViews();
        
    	((Button) findViewById(R.id.pictureButton))
        .setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {

                // in onCreate or any event where your want the user to
                // select a file
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
        });
    }
    
    private void setupViews(){
        Bitmap grayScale = toGrayscale(base);
        Bitmap cropped= crop(grayScale);
        //grayScale.recycle();
        start.setImageBitmap(base);
        gray.setImageBitmap(cropped);
        ArrayList<boolean[][]> results = scaleBitmap(cropped,30,30,findIndices(cropped));
        //cropped.recycle();
        //crop.setImageBitmap(toBitmap(result.get(0)));
        for (boolean[][] chr: results) {
        	ImageView image = new ImageView(this);
        	image.setMaxHeight(30);
        	image.setMaxWidth(30);
        	image.setImageBitmap(toBitmap(chr));
        	image.setVisibility(View.VISIBLE);
        	image.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_math_ocr, menu);
        return true;
    }
    
    public Bitmap toBitmap(boolean[][] input) {
    	Bitmap output = Bitmap.createBitmap(30, 30, Bitmap.Config.RGB_565);
    	for(int i = 0; i < 30; ++i) 
    		for(int j = 0; j < 30; ++j)
    			if (input[i][j])
    				output.setPixel(j, i, Color.BLACK);
    			else
    				output.setPixel(j,i,Color.WHITE);
  
    	return output;

    }
    
    public Bitmap toGrayscale(Bitmap input) {
    	Bitmap grayScaled = Bitmap.createBitmap(input.getWidth(),input.getHeight(),Bitmap.Config.RGB_565);
		int avg = 0;
		int black = 0;
		int white = -1;
		int height = input.getHeight();
		int width = input.getWidth();
		
    	for(int i = 0; i < height; ++i) {
    		for(int j = 0; j < width; ++j) {
    			// color is really just an int
    			avg = (input.getPixel(j, i) >> 16) & 0xFF;
    			avg += (input.getPixel(j, i) >> 8) & 0xFF;
    			avg += (input.getPixel(j, i) & 0xFF);
    			avg /= 3;
    			//avg = (avg << 16) | (avg << 8) | avg;
    			if (avg  < threshold)
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
    	
    	int height = input.getHeight();
    	int width = input.getWidth();
    	
    	// Find top and bottom indices
    	
    	for(int i = 0; i < height; ++i) {
    		boolean found = false;
    		// Check each row
    		for(int j = 0; j < width; ++j) {
    			if( (input.getPixel(j, i) & 0xFF ) < THRESH)
    				found = true;
    		}
    		if(found) {
    			topIndex = i;
    			break;
    		}
    	}
    	
    	for(int i = height - 1; i >= 0; --i) {
    		boolean found = false;
    		// Check each row
    		for(int j = 0; j < width; ++j) {
    			if( (input.getPixel(j, i) & 0xFF) < THRESH)
    				found = true;
    		}
    		if(found) {
    			botIndex = i;
    			break;
    		}
    	}
    	  	
    	Bitmap cropped = Bitmap.createBitmap(input.getWidth(),Math.abs(botIndex - topIndex),input.getConfig());
    	
    	int diff = botIndex - topIndex;
    	for(int i = 0; i < diff; ++i) {
    		for(int j = 0; j < width; ++j) {
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
    	int THRESH = threshold;
    	int foundThres = 15;
    	for(int i = 0; i < input.getHeight(); ++i) {
    		int found = 0;
    		// Check each row
    		for(int j = 0; j < input.getWidth(); ++j) {
    			if( (input.getPixel(j, i) & 0xFF ) < THRESH)
    				found++;
    		}
    		if(found > foundThres) {
    			topIndex = i;
    			break;
    		}
    	}
    	
    	for(int i = input.getHeight()-1; i >= 0; --i) {
    		int found = 0;
    		// Check each row
    		for(int j = 0; j < input.getWidth(); ++j) {
    			if( (input.getPixel(j, i) & 0xFF) < THRESH)
    				found++;
    		}
    		if(found>foundThres) {
    			botIndex = i;
    			break;
    		}
    	}
    	
    	
    	for(int i = 0; i < input.getWidth(); ++i) {
    		int found = 0;
    		// Check each row
    		for(int j = topIndex; j < botIndex; ++j) {
    			if( (input.getPixel(i, j) & 0xFF ) < THRESH)
    				found++;
    		}
    		if(found > foundThres) {
    			leftIndex = i;
    			break;
    		}
    	}
    	
    	for(int i = input.getWidth()-1; i >= leftIndex; --i) {
    		int found = 0;
    		// Check each row
    		for(int j = topIndex; j < botIndex; ++j) {
    			if( (input.getPixel(i, j) & 0xFF) < THRESH)
    				found++;
    		}
    		if(found > foundThres) {
    			rightIndex = i;
    			break;
    		}
    	}

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
    			//Log.v("ocrdebug","start: " + currentStart + " " + i);
    			foundStart = false;
    		}
    	}
    	
    	if (foundStart) {
	    	ArrayList<Integer> duple = new ArrayList<Integer>(2);
			duple.add(0, currentStart-1);
			duple.add(1, input.getWidth()-1);
			indices.add(duple);
    	}
    	
//    	for(ArrayList<Integer> duple: indices) {
//    		int red = (255 << 16);
//    		int green = (255 << 8);
//    		for(int j = 0; j < input.getHeight(); ++j) {
//    			input.setPixel(duple.get(0), j, red); 
//    		}
//    		for(int j = 0; j < input.getHeight(); ++j) {
//    			input.setPixel(duple.get(1), j, green); 
//    		}
//    	}
		
    	return indices;
    	
    }
    
    /* Presumably we want the images to all be the same size so that they
     * can be fed into the neural network with ease. However it can't hurt
     * to make the function generate boolean 2d arrays that can vary with
     * the input.
     */
    public ArrayList<boolean[][]> scaleBitmap(Bitmap source, int outputWidth, int outputHeight, ArrayList<ArrayList<Integer>> pairs) {
    	final int THRESH = 185;
    	final double OUTPUTTHRESH = .1;
    	int largestDim;
    	int imageWidth, imageHeight;
    	int topIndex, botIndex;
    	
    	Bitmap character;
    	
    	boolean[][] neuralInput;
    	ArrayList<boolean[][]> ret = new ArrayList<boolean[][]>(pairs.size());
    	
    	Log.v("asdf", "pairs size: " + pairs.size());
    	
    	// Loop over the pairs in the list.
    	//ArrayList<Integer> pair = pairs.get(1);
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
    				if( ((source.getPixel(j, i) & 0xFF) < THRESH) && (topIndex == 0) )
        				topIndex = i;
    				else if( (source.getPixel(j, i) & 0xFF) < THRESH)
    					botIndex = i;
    			}
    		}
    		
    		int temp = 0;
    		if(topIndex > botIndex){
    			temp = botIndex;
    			botIndex = topIndex;
    			topIndex = temp;
    		}
    		
    		// Get the image height.
    		imageHeight = Math.abs(botIndex - topIndex);
    		
    		if(imageWidth * imageHeight < 9570)
    			continue;
    		
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
    		
    		Log.v("asdf", "botIndex is: " + botIndex);
    		Log.v("asdf", "topIndex is: " + topIndex);
    		Log.v("asdf", "imageHeight is: " + imageHeight);
    		// now center the old bitmap image on the new bitmap (presumably the new image is wider, but it could be taller)
    		int startWidth = largestDim/2 - imageWidth/2;
    		int startHeight = largestDim/2 - imageHeight/2;
    		for(int i = 0; i < imageHeight; i++) {
    			for(int j = 0; j < imageWidth; j++){
    				if((source.getPixel(pair.get(0)+j, topIndex+i) & 0xFF) < THRESH)
    					character.setPixel(startWidth + j, startHeight + i, Color.BLACK);
    			}
    		}
    		
    		// Fill in the array of booleans according to the criterion that dictates a full pixel.
    		neuralInput = new boolean[outputHeight][outputWidth];
    		
    		int count;
    		int width = (int) Math.ceil(((double)largestDim)/outputWidth);
    		int height = (int) Math.ceil(((double)largestDim)/outputHeight);
    	
    		//crop.setImageBitmap(character);
    		for(int i = 0; i < outputHeight; i ++){
    			for(int j = 0; j < outputWidth; j++){
    				count = 0;
    				for(int imagei = i * height; imagei < (i+1) * height; imagei++) {
    					for(int imagej = j * width; imagej < (j+1) * width; imagej++){
    						if(imagej >= largestDim || imagei >= largestDim)
    							continue;
    						if((character.getPixel(imagej,imagei) & 0xFF) < THRESH)
    							count++;		
    					}
    				}
    				
    				if( ( (double) count)/(width * height) > 0)
    					neuralInput[i][j] = true;
    			}
    		}
    		character.recycle();
    		ret.add(neuralInput);
    	}
    	
    	return ret;
    }

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK) {
	        if (requestCode == SELECT_PICTURE) {
	            Uri selectedImageUri = data.getData();
	            selectedImagePath = getPath(selectedImageUri);
	            base.recycle();
	            base = BitmapFactory.decodeFile(selectedImagePath);
	            try {
					setupViews();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					
					e.printStackTrace();
					Log.e("debug", "No image found.");
				}
	        }
	    }
	}

	public String getPath(Uri uri) {
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    int column_index = cursor
	            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
}
