package com.acstech.churchlife.webservice;

import java.util.List;

import org.apache.http.HttpStatus;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
import com.acstech.churchlife.webservice.RESTClient.RequestMethod;

public class ApiOrganizations extends ApiBase {

	/*
	 * Return a list of organizations by search text and type
	 */
	public CorePagedResult<List<CoreOrganization>> organizations(int orgLevel, String searchText, int pageIndex) throws AppException {

		isOnlineCheck();
		
		CorePagedResult<List<CoreOrganization>> organizations = null;  		
		RESTClient client = getRESTClient("orgs"); 
		
		client.AddParam("q", searchText);
    	client.AddParam("pageIndex", Integer.toString(pageIndex));
        
        // if no orglevel specified...don't send anything on the querystring
        if (orgLevel > 0) {
        	client.AddParam("lvl", Integer.toString(orgLevel));
        }
    	                
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			organizations = CoreOrganization.GetCoreOrganizationPagedResult(client.getResponse());
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("ApiOrganizations.organizations");
    		info.getParameters().put("sitenumber", _siteNumber);
    		info.getParameters().put("username", _username);
    		info.getParameters().put("searchtext", searchText);
    		throw e;
    	}
		return organizations;
	}
	    
	/*
	 * Return a list of organization types 
	 */
	public List<CoreOrganizationType> organizationtypes() throws AppException {

		isOnlineCheck();
		
		List<CoreOrganizationType> organizationtypes = null;  		
		RESTClient client = getRESTClient("types/orgleveltypes"); 	    
    	    	    	              
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			organizationtypes = CoreOrganizationType.GetCoreOrganizationTypeList(client.getResponse());
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("ApiOrganizations.organizationtypes");
    		info.getParameters().put("sitenumber", _siteNumber);
    		info.getParameters().put("username", _username);
    		throw e;
    	}
		return organizationtypes;
	}
	

    

	/*
	
	public CoreOrganizationDetail organization(int orgId) throws AppException {

		isOnlineCheck();
		
		CoreOrganizationDetail organization = null;
		RESTClient client = getRESTClient(String.format("orgs/%s", orgId));
    		    	
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			organization = CoreOrganizationDetail.GetCoreOrganizationDetail(client.getResponse());
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("Api.Organizations");
    		info.getParameters().put("sitenumber", siteNumber);
    		info.getParameters().put("username", username);
    		info.getParameters().put("indvid", indvId);
    		throw e;
    	}
		return organization;
	}
	

    public CorePagedResult<List<CoreOrganizationStaff>> OrganizationStaff(int orgId, int pageIndex)
    {
        var parms = new Dictionary<string, string>
        {
            { "pageIndex", pageIndex.ToString() }
        };
        return JsonConvert.DeserializeObject<CorePagedResult<List<CoreOrganizationStaff>>>(GET(String.Format("orgs/{0}/staff", orgId), parms));
    }
    
	 */
	
	// CTOR
	public ApiOrganizations(String webServiceUrl, String applicationId, int siteNumber, String username, String password) {
		super(webServiceUrl, applicationId, siteNumber, username, password);
	}
}
