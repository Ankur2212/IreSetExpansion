package com.team11.searchAPI;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FarooSearch {
	public set<String> SearchFaroo(String query,int records){
		int starting=1;
		Set<String> result=new HashSet<String>();
		Set<String> finalresult=new HashSet<String>();
		URL url;
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.iiit.ac.in", 8080));
		HttpURLConnection conn=null;
		BufferedReader br=null;
		String FarooKey=CommonConstants.arshFarooKey;
		try{
			while(starting<records){
				String start=Integer.toString(starting);
				query=query.replaceAll(" ","%20");
				url=new URL("http://www.faroo.com/api?q="+query+"&start="+start+"&length=10&l=en&src=web&i=false&f=json&key="+FarooKey);
				conn=(HttpURLConnection) url.openConnection(proxy);
				conn.setRequestMethod("GET");
				br=new BufferedReader(new InputStreamReader((conn.getInputStream())));
				StringBuilder sb=new StringBuilder();
				String line;
				while((line=br.readLine())!=null)
					sb.append(line);
				result=jsonParser(sb.toString());
				if(result.size()==0) break;
				for(String local:result) finalresult.add(local);
				starting=finalresult.size();
			}
		}catch(Exception E){
			System.out.println("Error Retreiving Results");
		}
		return finalresult;
	}
	public Set<String> jsonParser(String s) throws JSONException{
		Set<String> output=new HashSet<String>();
		JSONObject obj=new JSONObject(s);
		JSONArray arr=obj.getJSONArray("results");
		for(int i=0;i<arr.length();i++){
			String geturl=arr.getJSONObject(i).getString("url");
			output.add(geturl);
		}
		return output;
	}
}
