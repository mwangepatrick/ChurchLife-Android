package com.acstech.churchlife.webservice;

public class CorePagedResult<T> {

	public T Page;
	public int PageCount;
	public int PageIndex;
	public int PageSize;

    private String _sourceJson;
    public String toString() {
  	  return _sourceJson;		
	}
    
    public CorePagedResult(String json) {
    	_sourceJson = json;
    }
    
}
