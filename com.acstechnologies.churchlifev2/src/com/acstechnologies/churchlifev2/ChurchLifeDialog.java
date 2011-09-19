package com.acstechnologies.churchlifev2;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class ChurchLifeDialog extends Dialog {

    private String _name;
    private ReadyListener _readyListener;
    
	
	
	public interface ReadyListener {
		public void ready(String name);
	}

	
	
	
	public ChurchLifeDialog(Context context, String name, ReadyListener readyListener) {
		super(context);
		_name = name;
		_readyListener = readyListener;
				
		
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		/** Design the dialog in main.xml file */
		//setContentView(R.layout.main);
		
		//okButton = (Button) findViewById(R.id.OkButton);
		//okButton.setOnClickListener(this);
			
	}
	
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.dialog_networkerror);
	        //setTitle("Enter your Name ");
	        
	        Button buttonOK = (Button) findViewById(R.id.buttonOk);
	        buttonOK.setOnClickListener(new OKListener());
	        //etName = (EditText) findViewById(R.id.EditText01);
	    }
	
	    private class OKListener implements android.view.View.OnClickListener {
	        @Override
	        public void onClick(View v) {
	           // _readyListener.ready(String.valueOf(etName.getText()));
	            ChurchLifeDialog.this.dismiss();
	        }
	    }
	    
}