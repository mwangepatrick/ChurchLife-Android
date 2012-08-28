package com.acstech.churchlife;


import java.util.ArrayList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;

// Performs a web service call on another thread.
//
// Caller should show progress dialog prior to calling and hide it after complete.

/**]
 * Where T is the listitem that gets bound to a list adapter 
 * 
 * @author softwarearchitect
 *
 * @param <T>
*/
public abstract class ListLoaderBase<T> {
	
	protected abstract ArrayList<T> getList();	
	protected abstract void getWebserviceResults() throws AppException; 
	protected abstract void buildItemList() throws AppException;
		
	private String _noResultsMessage = "Sorry, no record found.";
	private String _nextResultsMessage = "More Results";
	private String _previousResultsMessage = "Previous Results";
	
	protected int _pageIndex = -1;								
	protected Throwable _exception;	
	protected Runnable _postRun;				// passed to load - method to execute when load is complete
		
	public String getNoResultsMessage() {
		return _noResultsMessage;
	}	
	public void setNoResultsMessage(String value) {
		_noResultsMessage = value;
	}

	public String getNextResultsMessage() {
		return _nextResultsMessage;
	}
	public void setNextResultsMessage(String value) {
		_nextResultsMessage = value;
	}
	
	public String getPreviousResultsMessage() {
		return _previousResultsMessage;
	}
	public void setPreviousResultsMessage(String value) {
		_previousResultsMessage = value;
	}
		
	public void clear()
	{
		ArrayList<T> list = getList();		
		if (list != null) {
			list.clear();
		}		
		_pageIndex = -1;
	}
	
	public boolean success() {
		return (_exception == null);		
	}
	
	public Throwable getException() {
		return _exception;	
	}
	
	
	/**
	 * 
	 *  
	 * @param onLoaded
	 * @throws AppException
	 */
	public void LoadNext(Runnable onLoaded) throws AppException {
		if (getList() != null) {
			Load(_pageIndex+1, onLoaded);
		}
		else {
			Load(0, onLoaded);
		}		
	}
	
	public void LoadPrevious(Runnable onLoaded) throws AppException {
		if (_pageIndex > 0) {
			Load(_pageIndex-1, onLoaded);
		}	
		else {
			Load(0, onLoaded);
		}
	}
	
	public void Load(int pageIndex, Runnable onLoaded) throws AppException {
		
		_pageIndex = pageIndex;
		_postRun = onLoaded;
		
		// This handler is called once the search is complete.  It looks at the data returned
    	//  from the thread (in the Message) to determine success/failure. 
    	final Handler handler = new Handler() {
    		public void handleMessage(Message msg) {    			    
    			try {
	    			if (msg.what == 0) {
	    				
	    				buildItemList();	    			
	    				
	          	 		if (_postRun != null) {
	           	 			_postRun.run();	
	           	 		}          	 		    			
	       			}		    						    		
	       			else if (msg.what < 0) {
	       				// If < 0, the exception is in the message bundle.  Save it
	       				Bundle b = msg.getData();
	       				_exception = ExceptionHelper.getAppExceptionFromBundle(b, "ListLoader.handleMessage");	    				
	       			} 
    			}
    			catch (Exception e) {
    				_exception = e;			    				
    			}    
    		}
    	};
    	
    	Thread searchThread = new Thread() {  
    		public void run() {
    			try {    				
    				getWebserviceResults();    				    		
	    	    	handler.sendEmptyMessage(0);
    			}
    			catch (Exception e) {    				
    				ExceptionHelper.notifyNonUsers(e);			// Log the full error, 
    				
    				Message msg = handler.obtainMessage();
    				msg.what = -1;
    				msg.setData(ExceptionHelper.getBundleForException(e));	
    				handler.sendMessage(msg);    				
    			}    			       	    	    	    	
    		 }
    	};
    	searchThread.start();
	}
	
}