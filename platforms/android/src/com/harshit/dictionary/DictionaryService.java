package com.harshit.dictionary;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DictionaryService {
	/*
	 * private Context context;
	 * 
	 * public DictionaryService(Context _context) { context = _context; }
	 */
	void define(String word, CallbackContext callbackContext) throws Exception {
		// http://services.aonaware.com/DictService/DictService.asmx
		// http://services.aonaware.com/webservices/Define
		/*
		 * 
		 * <?xml version="1.0" encoding="utf-8"?> <soap:Envelope
		 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		 * xmlns:xsd="http://www.w3.org/2001/XMLSchema"
		 * xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"> <soap:Body>
		 * <Define xmlns="http://services.aonaware.com/webservices/">
		 * <word>string</word> </Define> </soap:Body> </soap:Envelope>
		 */
		String rawRequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "  <soap:Body>"
				+ "    <Define xmlns=\"http://services.aonaware.com/webservices/\">"
				+ "      <word>" + word + "</word>" + "    </Define>"
				+ "  </soap:Body>" + "</soap:Envelope>";
		String SOAPAction = "http://services.aonaware.com/webservices/Define";
		String url = "http://services.aonaware.com/DictService/DictService.asmx";
		offLoadTask backgroudTask = new offLoadTask(callbackContext);
		bckModel model = new bckModel();
		model.url = url;
		model.soapAction = SOAPAction;
		model.rawRequest = rawRequest;
		backgroudTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, model);
	}
}

class offLoadTask extends AsyncTask<bckModel, Void, JSONObject> {
	private CallbackContext callbackContext;

	public offLoadTask(CallbackContext _callbackContext) {
		this.callbackContext = _callbackContext;
	}

	private String getNodeValue(Element result, String namespace, String Tag) {
		if (result.getElementsByTagNameNS(namespace, Tag) != null
				&& result.getElementsByTagNameNS(namespace, Tag).getLength() > 0) {

			if ((Node) result.getElementsByTagNameNS(namespace, Tag).item(0)
					.getFirstChild() != null) {
				return result.getElementsByTagNameNS(namespace, Tag).item(0)
						.getFirstChild().getNodeValue();
			}
			return "";
		}
		return "";
	}

	private String getNodeValue(Element result, String Tag) {
		if (result.getElementsByTagName(Tag) != null
				&& result.getElementsByTagName(Tag).getLength() > 0) {

			if ((Node) result.getElementsByTagName(Tag).item(0).getFirstChild() != null) {
				return result.getElementsByTagName(Tag).item(0).getFirstChild()
						.getNodeValue();
			}
			return "";
		}
		return "";
	}
	
	protected JSONObject doInBackground(bckModel... _obj) {
		JSONObject json = new JSONObject();
		bckModel obj = _obj[0];
		String val = "";
		val = makeRequest(obj.rawRequest, obj.soapAction, obj.url);
		try {
			JSONObject temp = getDictJSON(val);

			json.put("SUCCESS", "TRUE");
			if (!temp.has("ERROR")) {

				json.put("DATA", temp);
			} else {
				json.put("ERROR", temp);
			}
			return json;
		} catch (Exception e) {
			LOG.e("Error", e.toString());
			LOG.e("Error", e.getStackTrace()[0].toString());
			String msg = "{\"ERROR\":\"" + e.toString() + "\"}";
			callbackContext.error(msg);
		}
		return json;
	}

	protected void onPostExecute(JSONObject resp) {
		try {
			if (resp != null
					&& !(resp.getString("SUCCESS").equalsIgnoreCase("false"))) {
				LOG.i("JSON obj", resp.toString());
				PluginResult result = new PluginResult(PluginResult.Status.OK,
						resp);
				result.setKeepCallback(false);
				callbackContext.sendPluginResult(result);
			} else {
				PluginResult result = new PluginResult(
						PluginResult.Status.ERROR,
						"{\"ERROR\":\"SOMTHING IS WRONG\"}");
				result.setKeepCallback(false);
				callbackContext.sendPluginResult(result);
			}
		} catch (Exception e) {
			PluginResult result = new PluginResult(PluginResult.Status.ERROR,
					"{\"ERROR\":\"SOMTHING IS WRONG\"}");
			result.setKeepCallback(false);
			callbackContext.sendPluginResult(result);
		}
	}

	private String makeRequest(String rawRequest, String SOAPAction, String URL) {
		try {
			StringBuffer rspString = new StringBuffer();
			HttpURLConnection con = (HttpURLConnection) (new URL(URL))
					.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
			con.setRequestProperty("Content-Length",
					String.valueOf(rawRequest.getBytes().length));
			//con.setRequestProperty("Accept-Encoding", "gzip,deflate");
			LOG.i("Length:", String.valueOf(rawRequest.length()));
			LOG.i("ByteLength:", String.valueOf(rawRequest.getBytes().length));
			con.setRequestProperty("SOAPAction", SOAPAction);
			con.setDoInput(true);
			con.setDoOutput(true);
			con.getOutputStream().write(rawRequest.getBytes());
			con.connect();
			int reqStatus = con.getResponseCode();
			LOG.i("Status Code", String.valueOf(reqStatus));
			if (reqStatus == 200) {
				InputStream is = con.getInputStream();
				// InputStream is = con.getInputStream();
				BufferedReader r = new BufferedReader(new InputStreamReader(is));
				// StringBuilder total = new StringBuilder();
				String line;
				while ((line = r.readLine()) != null) {
					rspString.append(line);
				}
				con.disconnect();
				// appendLog("Response********");
				// appendLog(rspString.toString());
				Log.i("Request", "val:" + rawRequest);
				Log.i("Response", "val==" + rspString.toString());
				return rspString.toString();
			}
			return "_MYC0lt_432_ERR0R_INVALID Response from server, status code not 200 but "
					+ reqStatus;
		} catch (Exception e) {
			LOG.e("WEB RESPONSE DATA", e.toString());
			return "_MYC0lt_432_ERR0R_" + e.toString();
		}

	}

	JSONObject getDictJSON(String webResponse) throws Exception {
		JSONObject jsonObj = new JSONObject();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);

			DocumentBuilder db = factory.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(webResponse));
			LOG.i("Response that we got", webResponse);
			Document doc = db.parse(is);
			Element currentElement;
			doc.getDocumentElement().normalize();
			String soap = "http://schemas.xmlsoap.org/soap/envelope/";
			currentElement = (Element) doc.getElementsByTagNameNS(soap, "Body")
					.item(0);
			if (currentElement != null) {
				currentElement = (Element) currentElement
						.getElementsByTagNameNS(
								"http://services.aonaware.com/webservices/",
								"DefineResponse").item(0);
				// DefineResponse
				if (currentElement != null) {
					currentElement = (Element) currentElement
							.getElementsByTagName("DefineResult").item(0);
					if (currentElement != null) {
						NodeList definitionList = currentElement
								.getElementsByTagName("Definition");
						JSONArray definitionArray = new JSONArray();
						for (int i = 0; i < definitionList.getLength(); i++) {
							Element definitionEl = (Element) definitionList
									.item(i);
							JSONObject tempObj = new JSONObject();
							String definition = getNodeValue(definitionEl, 
									"WordDefinition");
							String dictionaryName = getNodeValue(definitionEl,
									 "Dictionary");
							tempObj.put("WordDefinition", definition);
							tempObj.put("Dictionary", dictionaryName);
							definitionArray.put(tempObj);
						}
						jsonObj.put("Definitions", definitionArray);
						return jsonObj;
					}// end of defineResult
				}// end of defineResponse

			}// end of body
		} catch (Exception e) {
			jsonObj.put("Error", e.toString());
			jsonObj.put("STATUS", "false");
			return jsonObj;
		}
		return jsonObj;
	}

}

class bckModel {
	String url;
	String soapAction;
	String rawRequest;
	// CallbackContext callbackContext;
}
