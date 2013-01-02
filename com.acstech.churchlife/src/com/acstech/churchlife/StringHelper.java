package com.acstech.churchlife;

public class StringHelper {

	final static String NULL = "null";
	
    public static String NullOrEmpty(String input) {
    	
    	if (input == null) {
    		return "";
    	}
    	else if (input.equals(NULL)) {
    		return "";
    	}
    	else {
    		return input;
    	}    	    
    }
    
}
