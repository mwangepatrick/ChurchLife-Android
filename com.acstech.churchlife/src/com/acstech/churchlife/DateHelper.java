package com.acstech.churchlife;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class DateHelper {


    public static Date StringToDate(String input) throws AppException
    { 
    	return StringToDate(input, "MM/dd/yyyy hh:mm:ss a");
    }
    
    public static Date StringToDate(String input, String format) throws AppException
    {    	  
    	SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
		try {
			return dateFormatter.parse(input);
		} catch (ParseException e) {
			throw AppException.AppExceptionFactory(e,
					   ExceptionInfo.TYPE.UNEXPECTED,
					   ExceptionInfo.SEVERITY.CRITICAL, 
					   "100",           												    
					   "StringToDate",
					   String.format("Error attempting to format the date %s.", input)); 
		}
    }
 
}
