package com.acstech.churchlife;

public class BooleanHelper {

		public static boolean ParseBoolean(String inputValue) {
			
			if (inputValue.toUpperCase().equals("Y")) {
				return true;
			}
			else if (inputValue.toUpperCase().equals("N")) {	
				return false;
			}	
			else if (inputValue.toUpperCase().equals("TRUE")) {	
				return true;
			}	
			else if (inputValue.toUpperCase().equals("FALSE")) {	
				return false;
			}				
			else {
				throw new IllegalArgumentException();
			}
		}
		
		public static boolean ParseBoolean(int inputValue) {
			if (inputValue == 0) {
				return true;
			}
			else if (inputValue != 0) {
				return false;
			}	
			else {
				throw new IllegalArgumentException();
			}
		}
		
}
