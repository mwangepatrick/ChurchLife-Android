package com.acstech.churchlife.webservice;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class CoreAccountMerchant extends CoreObject {

	public UUID SolutionId;
	public UUID MerchantAccountId;
	public UUID DepositAccountId;
	public boolean AllowCreditDebitCards;
	public boolean AllowACH;
	public boolean DebitCardsOnly;
	public boolean NonMemberGivingEnabled;

    // Factory Method - parse json
    public static CoreAccountMerchant GetCoreAccountMerchant(String json) throws AppException
    {    	 
    	CoreAccountMerchant am = new CoreAccountMerchant();
  	  	am._sourceJson = json;
  	  
  	  	try
  	  	{
  	  		JSONObject jo = new JSONObject(json);
	
  	  		am.SolutionId = UUID.fromString(jo.getString("SolutionId"));  	  		
  	  		am.MerchantAccountId = UUID.fromString(jo.getString("MerchantAccountId"));
  	  		am.DepositAccountId = UUID.fromString(jo.getString("DepositAccountId"));
  	  		am.AllowCreditDebitCards = jo.getBoolean("AllowCreditDebitCards");
  	  		am.AllowACH = jo.getBoolean("AllowACH");
  			am.DebitCardsOnly = jo.getBoolean("DebitCardsOnly");
  			am.NonMemberGivingEnabled = jo.getBoolean("NonMemberGivingEnabled"); 
  	  	}
  	  	catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreAccountMerchant.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

		    throw AppException.AppExceptionFactory(e, i); 
  	  	}	
  	  	return am;		
    }
    
}
