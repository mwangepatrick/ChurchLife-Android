package com.acstech.churchlife;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.acstech.churchlife.exceptionhandling.ExceptionHelper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DialogImageViewFragment extends DialogFragment {
	
	private TextView _loadingTextView;
	private ImageView _imageToView;
	private String _imageUrl = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setCancelable(true);	   
	    setStyle(DialogFragment.STYLE_NO_TITLE, 0);
	    //setStyle(DialogFragment.STYLE_NO_FRAME, 0);	    
	}

	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 View myFragmentView = inflater.inflate(R.layout.imageview, container, false);

		 _imageToView = (ImageView)myFragmentView.findViewById(R.id.imageToView);
		 _loadingTextView = (TextView)myFragmentView.findViewById(R.id.loadingTextView);
		 
		 Bundle b = this.getArguments();		 		
		 _imageUrl = b.getString("imageurl");		 
		 new loadImageUrlTask().execute(_imageUrl);
		 
		 return myFragmentView;
	 }
		
	 
     /**
      * Handles loading the image of the person/family in the background
      *  
      * @author softwarearchitect
      *
      */
     private class loadImageUrlTask extends AsyncTask<String, Void, Drawable> {    	
        @Override
        protected Drawable doInBackground(String... imageUrl) {        	
        	return ImageOperations(imageUrl[0]);                              		        
        }
        @Override
        protected void onPostExecute(Drawable image) {
        	if (image != null) {        		
        		int width = image.getIntrinsicWidth();
        		int height = image.getIntrinsicHeight();        		
        		//String message = String.format("%s(w) - %s(h)", width, height);		 
       		 	//Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
       		 	        	        		    		  
       		 	_imageToView.setImageDrawable(image);     		
       		 	_imageToView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
    				 													   LinearLayout.LayoutParams.WRAP_CONTENT));
    		 	_imageToView.getLayoutParams().width = width;
    		 	_imageToView.getLayoutParams().height = height;
        		       		 	
        		_loadingTextView.setVisibility(View.GONE);
        	}	 
        	else
        	{
        		_loadingTextView.setText(R.string.Dialog_UnableToLoadImage);
        	}
        }
      }


	    /**
	     * -gets a drawable for a given url
	     * -called from asynchronous task
	     * @param url
	     * @return
	     */
	    private Drawable ImageOperations(String url) {
	    	Drawable d = null;
	    	try {	    		
	    		if (url.length() > 0) {
	    			URL imageUrl = new URL(url);
	    			InputStream is = (InputStream) imageUrl.getContent();
	    			
	    			
	    			Bitmap b = BitmapFactory.decodeStream(is);
	    			b.setDensity(Bitmap.DENSITY_NONE);
	    			d = new BitmapDrawable(getResources(), b);
	    			//d = Drawable.createFromStream(is, "src");    	
	    		}
	    	   return d;	    	   
	    	} 
	    	catch (MalformedURLException e) {
	    		ExceptionHelper.notifyNonUsers(e);
	    		return null;
	    	} catch (IOException e) {
	    		ExceptionHelper.notifyNonUsers(e);
	    		return null;
	    	}
	    }
	    
}
