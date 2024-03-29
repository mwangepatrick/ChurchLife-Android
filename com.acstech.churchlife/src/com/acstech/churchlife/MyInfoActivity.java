package com.acstech.churchlife;

import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.R;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class MyInfoActivity extends ChurchlifeBaseActivity  {
	 	
	TextView txtName;
	TextView txtVersion;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
           	
        setTitle(R.string.Menu_MyInfo);
        setContentView(R.layout.myinfo);		
             
        bindControls();
        
        // Site Name
		GlobalState gs = GlobalState.getInstance(); 
		
        txtName.setText(gs.getSiteName());       
        
        // Application info
        txtVersion.setText(getVersion());
     
    }
    
    private void bindControls() {    	
    	txtName = (TextView)this.findViewById(R.id.txtOrganizationName);
    	txtVersion = (TextView)this.findViewById(R.id.txtVersion);      	
    }
    
    /**
     *  Reads and returns the version information from the manifest 
     *  
     * @return
     */
    private String getVersion() {
        
        PackageManager manager = this.getPackageManager();
        PackageInfo info;
        String result = "0.0";
        
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
			String prefix = getResources().getString(R.string.MyInfo_VersionPrefix);
			result = prefix + " " + info.versionName; 		       
		} catch (NameNotFoundException e) {
			ExceptionHelper.notifyNonUsers(e);		// do not raise any errors from here.  Non-catastrophic		
		}
    	return result;
    }
        
}
