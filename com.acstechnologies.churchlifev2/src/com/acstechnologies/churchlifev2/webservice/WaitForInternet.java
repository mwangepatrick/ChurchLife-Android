package com.acstechnologies.churchlifev2.webservice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.View;

import java.util.concurrent.atomic.AtomicBoolean;

import com.acstechnologies.churchlifev2.ChurchLifeDialog;

//credit to:  http://bjdodson.blogspot.com/2009/10/checking-for-internet-availability-on.html
public class WaitForInternet {
 	
	 /**
	  * Check for Internet connectivity.
	  * The calling context must have permission to
	  * access the device's network state.
	  *
	  * If the calling context does not have permission, an exception is thrown.
	  *
	  * @param WaitForInternetCallback
	  * @return 
	  */
	public static void setCallback(final WaitForInternetCallback callback) {  
		
		final ConnectivityManager connMan = (ConnectivityManager) callback.mActivity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
	
		final AtomicBoolean isConnected = new AtomicBoolean(connMan.getActiveNetworkInfo() != null && connMan.getActiveNetworkInfo().isConnected());
	  
		if (isConnected.get()) {
			callback.onConnectionSuccess();
			return;
		}
  
		final AtomicBoolean isRetrying = new AtomicBoolean(true);

		/* network dialog */			
		final ChurchLifeDialog dialog = new ChurchLifeDialog(callback.mActivity);
		dialog.setCancelable(false);
		
		dialog.setCloseListener(new View.OnClickListener() {
			public void onClick(View v) {
				synchronized (isRetrying) {
					isRetrying.set(false);
					isRetrying.notify();
					dialog.dismiss();  
				}
			}
		});
		
		dialog.setRetryListener(new View.OnClickListener() {
			public void onClick(View v) {
				synchronized (isRetrying) {
					isRetrying.notify();					
				}
			}
		});
		
//		
//		final AlertDialog.Builder connDialog = new AlertDialog.Builder(callback.mActivity);
//		connDialog.setTitle("Network not available");
//		connDialog.setMessage("Your phone cannot currently access the internet.");
//
//		connDialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialogInterface, int i) {
//				synchronized (isRetrying) {
//					isRetrying.notify();
//				}
//			}
//		});
//	  
//		connDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialogInterface, int i) {
//				synchronized (isRetrying) {
//					isRetrying.set(false);
//					isRetrying.notify();
//				}
//			}
//		});

		new Thread() {
			public void run() {
				while (!isConnected.get() && isRetrying.get()) {
					callback.mActivity.runOnUiThread(new Thread() {
						@Override
						public void run() {							
							dialog.show();
						}
					});

					synchronized (isRetrying) {
						try {
							isRetrying.wait();
						} catch (InterruptedException e) {
							Log.w("junction", "Error waiting for retry lock", e);
						}
					}

					isConnected.set((connMan.getActiveNetworkInfo() != null && connMan.getActiveNetworkInfo().isConnected()));
				}
    
				if (isConnected.get()) {
				     callback.onConnectionSuccess();
				} else {
				     callback.onConnectionFailure();
	    		}			
			}
		}.start();
	}
}	
