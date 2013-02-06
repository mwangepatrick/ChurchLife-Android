package com.acstech.churchlife;

import android.content.Context;
import android.widget.Toast;

public class WebViewGivingInterface {
	
    Context _context;

    /** Instantiate the interface and set the context */
    WebViewGivingInterface(Context c) {
        _context = c;
    }

    /** Show a toast from the web page */
    //@JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(_context, toast, Toast.LENGTH_SHORT).show();
    }
    
}