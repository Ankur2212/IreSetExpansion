package com.team11.IreSetExpansion.searchAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.team11.IreSetExpansion.CommonConstants;


public class GoogleAPI implements CommonConstants{
	
	/**
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	private Set<String> jsonParser(String json) throws JSONException{
		Set<String> result = new HashSet<String>();
		JSONObject obj = new JSONObject(json);
		JSONArray arr = obj.getJSONArray("items");
		for (int i = 0; i < arr.length(); i++){
		    String post_id = arr.getJSONObject(i).getString("link");
		    result.add(post_id);
		}
		return result;
	}
	
	/**
	 * @param query
	 * @param totalRecords
	 * @return
	 */
	public Set<String> getDataFromGoogle(String query, int totalRecords) {
		Set<String> result = new HashSet<String>();	
		URL url;
		HttpURLConnection conn = null;
		BufferedReader br = null;
		query = query.replaceAll(" ", "%20");
		String accountKey=CommonConstants.ankurGoogleKey;
		try {
			url = new URL("https://www.googleapis.com/customsearch/v1?key="+accountKey+"&cx="+CommonConstants.ankurGoogleCx+"&q="+ query);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			StringBuilder sb = new StringBuilder();
			String output;
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}
			result=jsonParser(sb.toString());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}finally{
			try {
				br.close();
			} catch (IOException e) {}
			conn.disconnect();
		}
		
		return result;
	}
}