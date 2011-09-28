package com.acstechnologies.churchlifev2;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;

public class ChurchLifeDialog extends Dialog {

    private Button btnClose;
    private Button btnRetry;
	
    private View.OnClickListener _closeListener;
    private View.OnClickListener _retryListener;
    
    public void setCloseListener(View.OnClickListener listener) {
    	_closeListener = listener;
    }
    
    public void setRetryListener(View.OnClickListener listener) {
    	_retryListener = listener;
    }   
    
	// Constructor
	public ChurchLifeDialog(Context context) {
		super(context);

		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
	}
	
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.dialog_networkerror);
	        
	        getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);	// Make full screen dialog
	        
	        bindControls();						// Set state variables to their form controls	       
	    }
	
	  
	  /**
	  *  Links state variables to their respective form controls
	  */
	  private void bindControls(){
	    	
		  btnClose = (Button)this.findViewById(R.id.btnClose);	
		  btnClose.setOnClickListener(_closeListener);
	    	
		  btnRetry = (Button)this.findViewById(R.id.btnRetry);	
		  btnRetry.setOnClickListener(_retryListener);  
		  
		  // For now we are NOT showing a retry/close
		  btnClose.setVisibility(View.GONE);
		  btnRetry.setVisibility(View.GONE);		  
	  }
	    	    
}