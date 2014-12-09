package com.harshit.dictionary;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;

import com.harshit.dictionary.DictionaryService;

public class DictionaryPlugin extends CordovaPlugin {
	private DictionaryService service;

	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException 
	{

		try 
		{

			if (this.service == null) 
			{
				this.service = new DictionaryService();
			}// end of servie init
			if(action.equalsIgnoreCase("define")){
				String word=args.getString(0);
				getTheMeaning(word, callbackContext);
			}
		} 
		catch (Exception e) 
		{
			LOG.e("Excption at plugin",e.toString());
		}

		return true;
	}
	void getTheMeaning(String word,CallbackContext callbackContext)
	{
		try{
			service.define(word, callbackContext);
		}
		catch(Exception e){
			callbackContext.error("Webservice call failed "+e.toString());
		}
	}
}
