package com.acstech.churchlife;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

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
             
        	 // Get an 'unlocked' web service url to go to do giving
        	 GlobalState gs = GlobalState.getInstance();        	 
        	 ApiGiving apiCaller = new ApiGiving(_appPrefs.getGivingWebServiceUrl(), config.APPLICATION_ID_VALUE);					
        	 String url = apiCaller.givingUrl(gs.getUserName(), gs.getPassword(), gs.getSiteNumber());
        
        	 webViewer.loadUrl(url);
        }
        catch (AppException e) {
        	closeDialog();
    		ExceptionHelper.notifyUsers(e, GivingActivity.this);
    		ExceptionHelper.notifyNonUsers(e);    		
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
}
