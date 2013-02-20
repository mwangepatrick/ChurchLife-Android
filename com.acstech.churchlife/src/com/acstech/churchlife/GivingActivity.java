package com.acstech.churchlife;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.webservice.ApiGiving;

public class GivingActivity extends ChurchlifeBaseActivity {

	ProgressDialog _progress;
	AppPreferences _appPrefs;
	WebView webViewer;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	    	
        super.onCreate(savedInstanceState);
        
        try
        {    
        	_appPrefs = new AppPreferences(getApplicationContext());

        	 setTitle(R.string.Menu_Giving);    
        	 setContentView(R.layout.webview);
        	         	 
        	 bindControls();								// Set state variables to their form controls
        
        	 _progress = ProgressDialog.show(this, "", getString(R.string.Dialog_Loading), true);
        	 _progress.setCancelable(false);
          
        	 new loadGivingUrlTask().execute();	// login in background 
        }
    	catch (Exception e) {
    		closeDialog();
    		ExceptionHelper.notifyUsers(e, GivingActivity.this);
    		ExceptionHelper.notifyNonUsers(e);
    	}          
    }

    /**
     *  Links state variables to their respective form controls
     */
    private void bindControls(){    
    	webViewer = (WebView) findViewById(R.id.webViewer);    	
    	WebSettings settings = webViewer.getSettings();
    	    	
    	settings.setJavaScriptEnabled(true);
    	settings.setUserAgentString("ChurchLife.Android");  //zzz resource file - have page check for ios/android
    	
    	webViewer.addJavascriptInterface(new GivingWebViewInterface(this), "Android");    	
    	//webViewer.setWebViewClient(new WebViewClient());    	
    	webViewer.setWebViewClient(new WebViewClient() {
    	    @Override  
    	    public void onPageFinished(WebView view, String url) {
    	        super.onPageFinished(view, url);
    	        closeDialog();
    	    }  
    	    //@Override
    	    //public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
    	    //    Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
    	    //}
    	});
    
    }

    private void closeDialog() {
    	if (_progress != null) {
    		_progress.dismiss();
    	}
    }
    
    private class loadGivingUrlTask extends AsyncTask<Void, Void, String> {
    	
        @Override
        protected String doInBackground(Void... args) {
        	String url = "";    		
        	try {        		
        		// Get an 'unlocked' web service url to go to give 
       	 		GlobalState gs = GlobalState.getInstance();        	 
        		AppPreferences prefs = new AppPreferences(getApplicationContext());

       	 		ApiGiving apiCaller = new ApiGiving(prefs.getGivingWebServiceUrl(), config.APPLICATION_ID_VALUE);					
       	 		url = apiCaller.givingUrl(gs.getUserName(), gs.getPassword(), gs.getSiteNumber());
        	}
       	 	catch (AppException e) {
       	 		closeDialog();
       	 		ExceptionHelper.notifyNonUsers(e);    		
       	 	}
        	catch (Exception e) {
        		closeDialog();
        		ExceptionHelper.notifyNonUsers(e);
        	}          
        	return url;        		        	
        }

        @Override
        protected void onPostExecute(String url) {
        	if (url.length() > 0) {
        		webViewer.loadUrl(url);
        	}
        }
      }
          
}
