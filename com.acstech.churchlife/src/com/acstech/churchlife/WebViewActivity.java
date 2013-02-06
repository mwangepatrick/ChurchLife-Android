package com.acstech.churchlife;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.webservice.Api;
import com.acstech.churchlife.webservice.CoreAccountMerchant;

public class WebViewActivity extends ChurchlifeBaseActivity {

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
        
        	 // load data
        	 /*
        	  * this already loaded at login....
        	  * 
        	 GlobalState gs = GlobalState.getInstance(); 		
			 Api apiCaller = new Api(_appPrefs.getWebServiceUrl(), config.APPLICATION_ID_VALUE);					 
        	 CoreAccountMerchant merchantInfo = apiCaller.accountmerchant(gs.getUserName(), gs.getPassword(), gs.getSiteNumber());	
        	   */	 
        	 String s = "12123";
        }
    	catch (Exception e) {
    		ExceptionHelper.notifyUsers(e, WebViewActivity.this);
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
    	
    	webViewer.addJavascriptInterface(new WebViewGivingInterface(this), "Android");    	
    	webViewer.loadUrl("http://www.google.com");
    }

    // javascript close - re-route back to home page (
    
    
    
    
}
