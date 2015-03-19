package com.team11.searchAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.team11.CommonConstants;

public class BingAPI implements CommonConstants {

	/**
	 * @param query
	 * @param totalRecords
	 * @return
	 */
	public Set<String> getDataFromBing(String query, int totalRecords) {
		Set<String> result = new HashSet<String>();	
		URL url;
		HttpURLConnection conn = null;
		BufferedReader br = null;
		String accountKey=CommonConstants.ankurBingKey;

		try {
			query = query.replaceAll(" ", "%20");
			byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
			String accountKeyEnc = new String(accountKeyBytes);

			url = new URL("https://api.datamarket.azure.com/Data.ashx/Bing/Search/v1/Web?Query=%27" + query + "%27&$top="+totalRecords+"&$format=json");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
			br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			StringBuilder sb = new StringBuilder();
			String output;
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}
			result=jsonParser(sb.toString());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
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

	/**
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	private Set<String> jsonParser(String json) throws JSONException{
		Set<String> result = new HashSet<String>();
		JSONObject obj = new JSONObject(json);
		JSONArray arr = obj.getJSONObject("d").getJSONArray("results");
		for (int i = 0; i < arr.length(); i++){
			String post_id = arr.getJSONObject(i).getString("Url");
			result.add(post_id);
		}
		return result;
	}
}

