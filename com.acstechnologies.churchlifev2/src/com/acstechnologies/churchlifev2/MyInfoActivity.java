package com.acstechnologies.churchlifev2;

import android.os.Bundle;
import android.widget.TextView;

public class MyInfoActivity extends OptionsActivity  {
	 	
	TextView txtName;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myinfo);		
             
        bindControls();
        
        GlobalState gs = (GlobalState) getApplication();        
        txtName.setText(gs.getSiteName());        
    }
    
    
    private void bindControls() {    	
    	txtName = (TextView)this.findViewById(R.id.txtOrganizationName);    	
    }
    
}
