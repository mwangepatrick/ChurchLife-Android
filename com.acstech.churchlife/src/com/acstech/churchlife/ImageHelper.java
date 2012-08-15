package com.acstech.churchlife;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

public class ImageHelper {

	public static Bitmap getBackground (String hexColor)
	{
		return getBackground(Color.parseColor(hexColor));
	}
	
	public static Bitmap getBackground (int bgcolor)
	{
	try
	    {
	        Bitmap.Config config = Bitmap.Config.ARGB_8888; 	// Bitmap.Config.ARGB_8888 Bitmap.Config.ARGB_4444 to be used as these two config constant supports transparency
	        Bitmap bitmap = Bitmap.createBitmap(2, 2, config); 	// create a Bitmap
	 
	        Canvas canvas =  new Canvas(bitmap); 				// load the Bitmap to the Canvas
	        canvas.drawColor(bgcolor); 							// set the color
	 
	        return bitmap;
	    }
	    catch (Exception e)
	    {
	        return null;
	    }
	}

	
}
