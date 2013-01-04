//Alistair Dobke and Mark Mann
//Image processing for math OCR project
//http://cs.hmc.edu/~adobke/nn/

package com.example.math_ocr;


import java.util.ArrayList;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

public class ImageProcessor {

	public static final String newline = System.getProperty("line.separator");
	public static final String symbols = "0123456789+-";

	// The Character Recognizer currently requires 16x16 arrays to function.
	public static final int outputWidth = 16;
	public static final int outputHeight = 16;
	
	public static final char THRESHOLD = 100;

	public ImageProcessor() {
		// Nothing to do currently.
	}

	public static String processImage(Bitmap startImage, ImageView view)
	{
		char[][] cropped = crop(startImage);
		//Log.v("asdf","cropped has size: " + cropped.length + " x " + cropped[0].length);
		startImage.recycle();
		view.setImageBitmap(bitmapFromByteArray(cropped));
		ArrayList<ArrayList<Integer> > inds = findIndices(cropped);
		//Log.v("asdf","found indices: " + inds.size() );
		ArrayList<int[][]> result = scaleBitmap(cropped, outputWidth, outputHeight, inds);
		//Log.v("asdf","found this many results: " + result.size());
		CharacterRecognizer cr = new CharacterRecognizer();
		StringBuffer builder = new StringBuffer();
		int index = 0;
		for(int[][] array : result) {
			index = cr.whichCharacter(array);
			builder.append(symbols.charAt(index));
		}
		return builder.toString();
	}
	
	public static Bitmap bitmapFromByteArray(char[][] cropped) {
		Bitmap ret = Bitmap.createBitmap(cropped[0].length, cropped.length, Bitmap.Config.RGB_565);
		
		int white = -1, black = 0;
		for(int i = 0; i < cropped.length; i++){
			for(int j = 0; j < cropped[0].length; j++) {
				if(cropped[i][j] < 1){
					ret.setPixel(j, i, black);
				} else {
					ret.setPixel(j, i, white);
				}
			}
		}
		
		return ret;
	}
	
	public static char[][] crop(Bitmap input) {
		int leftIndex = -1;
		int rightIndex = -1;
		int topIndex = -1;
		int botIndex = -1;
		int THRESH = 100;
		int avg = 0;

		for(int i = 0; i < input.getHeight(); ++i) {
			boolean found = false;
			// Check each row
			for(int j = 0; j < input.getWidth(); ++j) {
				avg = (input.getPixel(j, i) >> 16) & 0xFF;
				avg += (input.getPixel(j, i) >> 8) & 0xFF;
				avg += (input.getPixel(j, i) & 0xFF);
				avg /= 3;
				if( (avg & 0xFF ) < THRESH)
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
				avg = (input.getPixel(j, i) >> 16) & 0xFF;
				avg += (input.getPixel(j, i) >> 8) & 0xFF;
				avg += (input.getPixel(j, i) & 0xFF);
				avg /= 3;
				if( (avg & 0xFF) < THRESH)
					found = true;
			}
			if(found) {
				botIndex = i;
				break;
			}
		}


		for(int j = 0; j < input.getWidth(); ++j) {
			boolean found = false;
			// Check each row
			for(int i = topIndex; i < botIndex; ++i) {
				avg = (input.getPixel(j, i) >> 16) & 0xFF;
				avg += (input.getPixel(j, i) >> 8) & 0xFF;
				avg += (input.getPixel(j, i) & 0xFF);
				avg /= 3;
				if( (avg & 0xFF ) < THRESH)
					found = true;
			}
			if(found) {
				leftIndex = j;
				break;
			}
		}

		for(int j = input.getWidth()-1; j >= leftIndex; --j) {
			boolean found = false;
			// Check each row
			for(int i = topIndex; i < botIndex; ++i) {
				avg = (input.getPixel(j, i) >> 16) & 0xFF;
				avg += (input.getPixel(j, i) >> 8) & 0xFF;
				avg += (input.getPixel(j, i) & 0xFF);
				avg /= 3;
				if( (avg & 0xFF) < THRESH)
					found = true;
			}
			if(found) {
				rightIndex = j;
				break;
			}
		}

		char[][] cropped = new char[botIndex-topIndex][rightIndex-leftIndex]; 

		char black = 0;
		char white = 127;
		
		int ioffset = topIndex;
		int joffset = leftIndex;
		for(int i = 0; i < botIndex - topIndex; ++i) {
			for(int j = 0; j < rightIndex - leftIndex; ++j) {
				// color is really just an int
				avg = (input.getPixel(j + joffset, i + ioffset) >> 16) & 0xFF;
				avg += (input.getPixel(j + joffset, i + ioffset) >> 8) & 0xFF;
				avg += (input.getPixel(j + joffset, i + ioffset) & 0xFF);
				avg /= 3;
				if(avg < THRESH) {
					cropped[i][j] = black;
					//Log.v("asdf","1");
				}
				else {
					cropped[i][j] = white;
					//Log.v("asdf","0");
				}
			}
		}

		return cropped;
	}

	public static ArrayList < ArrayList<Integer> > findIndices(char[][] input) {
		int currentStart = 1;
		char THRESH = THRESHOLD;
		ArrayList<ArrayList<Integer>> indices = new ArrayList<ArrayList<Integer>>();

		boolean foundStart = true;
		for(int i = 1; i < input[0].length; ++i) {
			boolean dark = false;
			// Check each row
			for(int j = 0; j < input.length; ++j) {
				if( input[j][i]  < THRESH) {
					dark = true;
					//Log.v("asdf","found dark");
				}
			}
			if(dark && !foundStart) {
				currentStart = i;
				foundStart = true;
			} else if (!dark && foundStart && ( (i - currentStart) > 4)) {
				ArrayList<Integer> duple = new ArrayList<Integer>(2);
				duple.add(0, currentStart-1);
				duple.add(1, i+1);
				indices.add(duple);
				Log.v("asdf","found somthing..");
				foundStart = false;
			}
		}

		if (foundStart) {
			ArrayList<Integer> duple = new ArrayList<Integer>(2);
			duple.add(0, currentStart-1);
			duple.add(1, input[0].length-1);
			indices.add(duple);
		}
		
		/*for(ArrayList<Integer> duple : indices) {
			int red = 255 << 16;
			int green = 255 << 8;
			for(int j = 0; j < input.getHeight(); j++) {
				input.setRGB(duple.get(0), j, red);
			}
			for(int j = 0; j < input.getHeight(); j++) {
				input.setRGB(duple.get(1), j, green);
			}
		}*/

		return indices;

	}

	/* Presumably we want the images to all be the same size so that they
	 * can be fed into the neural network with ease. However it can't hurt
	 * to make the function generate boolean 2d arrays that can vary with
	 * the input.
	 */
	public static ArrayList<int[][]> scaleBitmap(char[][] source, int outputWidth, int outputHeight, ArrayList<ArrayList<Integer>> pairs) {
		final int THRESH = THRESHOLD;
		int largestDim;
		int imageWidth, imageHeight;
		int topIndex, botIndex;

		char[][] character;

		int[][] neuralInput;
		ArrayList<int[][]> ret = new ArrayList<int[][]>(pairs.size());


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
			for(int i = 0; i < source.length; i++) {
				for(int j = pair.get(0); j < pair.get(1); j++){
					if( (source[i][j] < THRESH) && (topIndex == 0) )
						topIndex = i;
					else if(source[i][j] < THRESH)
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

			if(imageWidth * imageHeight < 100)
				continue;

			if(imageWidth > imageHeight)
				largestDim = imageWidth;
			else
				largestDim = imageHeight;

			// now create a new largest dimension^2 Bitmap to hold our scaled image.
			character = new char[largestDim][largestDim]; //(largestDim, largestDim, BufferedImage.TYPE_INT_RGB);
			
			char white = 255;
			char black = 0;

			// White out all the pixels in the new Bitmap. Doing this as a precaution.
			for(int i = 0; i < largestDim; i++) {
				for(int j = 0; j < largestDim; j++) {
					character[i][j] = white;
					//character.setRGB(j, i, white);
				}
			}

			// now center the old bitmap image on the new bitmap (presumably the new image is wider, but it could be taller)
			int startWidth = largestDim/2 - imageWidth/2;
			int startHeight = largestDim/2 - imageHeight/2;
			for(int i = 0; i < imageHeight; i++) {
				for(int j = 0; j < imageWidth; j++){
					//if((source.getPixel(pair.get(0)+j, topIndex+i) & 0xFF) < THRESH)
					//	character.setRGB(startWidth + j, startHeight + i, black);
					if(source[topIndex+i][pair.get(0)+j] < THRESH)
						character[startHeight + i][startWidth + j] =  black;
				}
			}

			// Fill in the array of booleans according to the criterion that dictates a full pixel.
			neuralInput = new int[outputHeight][outputWidth];

			int count;
			int width = (int) Math.ceil(((double)largestDim)/outputWidth);
			int height = (int) Math.ceil(((double)largestDim)/outputHeight);

			for(int i = 0; i < outputHeight; i ++){
				for(int j = 0; j < outputWidth; j++){
					count = 0;
					for(int imagei = i * height; imagei < (i+1) * height; imagei++) {
						for(int imagej = j * width; imagej < (j+1) * width; imagej++){
							if(imagej >= largestDim || imagei >= largestDim)
								continue;
							if(character[imagei][imagej] < THRESH)
								count++;		
						}
					}

					if(count > 0)
						neuralInput[i][j] = 1;
					else
						neuralInput[i][j] = 0;
				}
			}
			ret.add(neuralInput);
		}

		return ret;
	}

	/*
	public static void main(String[] args) {
		String name = "54-47";
		StringBuffer filepath = new StringBuffer();
		filepath.append(System.getProperty("user.dir"));
		filepath.append(File.separator);
		filepath.append(name + ".jpg");
		
		Parser parse = new Parser();
		String result = printCharacters(new File(filepath.toString()), name);
		System.out.println(result);
		System.out.println(parse.parse(result));
	}
	*/
}
