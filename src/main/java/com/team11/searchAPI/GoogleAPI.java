package src.main.java.com.team11.searchAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import src.main.java.com.team11.CommonConstants;
import src.main.java.com.team11.CommonUtilities.ListUtil;
import src.main.java.com.team11.CommonUtilities.LogUtil;
import src.main.java.com.team11.Parser.ListFinderHTML;
import src.main.java.com.team11.Parser.WebList;
import src.main.java.com.team11.Parser.WebPage;


public class GoogleAPI implements CommonConstants{

	public static ArrayList<WebPage> googleSearch(ArrayList<String> seedList,String concept, int noOfResults, double overlapTolerance,String query) {
		ArrayList<WebPage> listPages = new ArrayList<WebPage>();
		URL url;
		HttpURLConnection conn = null;
		BufferedReader br = null;
		ListFinderHTML myfinder = new ListFinderHTML();
		try {
			query = query.replaceAll(" ", "%20");
			url = new URL("https://www.googleapis.com/customsearch/v1?key="+CommonConstants.ankurGoogleKey+"&cx="+CommonConstants.ankurGoogleCx+"&q="+ query);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			/*Reader reader = new InputStreamReader(url.openStream(), charset);
			GoogleResults results = new Gson().fromJson(reader, GoogleResults.class);

			for(Result r :results.getResponseData().getResults()){
				//System.out.println(r);
				urls.add(r.getUrl());
			}*/
			StringBuilder sb = new StringBuilder();
			String output;
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}
			JSONObject obj;
			
			obj = new JSONObject(sb.toString());
			JSONArray arr = obj.getJSONArray("items");
			for (int i = 0; i < arr.length(); i++){
				String post_id = arr.getJSONObject(i).getString("link");
				String description=arr.getJSONObject(i).getString("snippet");
				String title=arr.getJSONObject(i).getString("title");
				myfinder.SetHTML(post_id);
				LogUtil.log.info("aaa "+title+" "+ post_id+" "+description);
				WebPage page = new WebPage(title, post_id,description);
				ArrayList<String> webList= new ArrayList<String>();
				while ((webList = myfinder.getNextList())!=null && webList.size()>0) {
					if(ListUtil.getOverLap(webList,seedList)>=(seedList.size()*overlapTolerance)){
						page.addList(new WebList(webList, myfinder.getHeader(), myfinder.getDescription()));
					}
				}
				listPages.add(page);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listPages;
	}
}