//Alistair Dobke and Mark Mann
//Math OCR project
//http://cs.hmc.edu/~adobke/nn/

package com.example.math_ocr;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MathOcr extends Activity {
	private static final int SELECT_PICTURE = 1;
	private ImageProcessor imageProc;
	private Parser parser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_math_ocr);
		
		imageProc = new ImageProcessor();
		parser = new Parser();
		
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

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		( (TextView) findViewById(R.id.strbox) ).setText("processing");
	    if (resultCode == RESULT_OK) {
	        if (requestCode == SELECT_PICTURE) {
	            Uri selectedImageUri = data.getData();
	            String selectedImagePath = getPath(selectedImageUri);
	            Bitmap base = BitmapFactory.decodeFile(selectedImagePath);
	            try {
					String result = ImageProcessor.processImage(base, (ImageView) findViewById(R.id.image));
					Log.v("adsf", result);
					int solution = parser.parse(result);

					( (TextView) findViewById(R.id.solbox) ).setText(Integer.toString(solution));
					( (TextView) findViewById(R.id.strbox) ).setText(result);
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_math_ocr, menu);
		return true;
	}

}
