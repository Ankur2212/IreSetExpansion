package src.main.java.com.team11.webDB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import src.main.java.com.team11.CommonConstants;
import src.main.java.com.team11.CommonUtilities.LogUtil;
import src.main.java.com.team11.searchAPI.BingAPI;
import src.main.java.com.team11.searchAPI.GoogleAPI;
import src.main.java.com.team11.webDB.SearchResult;
import src.main.java.com.team11.Parser.WebPage;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class SearchProvider {
	private static MongoClient mongoClient;
	private static DB db;
	private static DBCollection webCollection;
	private static DBCollection searchCollection;
	
	static {
		try{
			System.setProperty("https.proxyHost", "proxy.iiit.ac.in");
			System.setProperty("https.proxyPort", "8080");

			mongoClient = new MongoClient();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		db = mongoClient.getDB(CommonConstants.GOOGLE_SEARCH_ENGINE);
		webCollection = db.getCollection("urlCollection");
		searchCollection = db.getCollection("searchCollection");
	}
	
	public static ArrayList<WebPage> getSearchResults(ArrayList<String> seedList, String concept, int noOfResults, double overlapTolerance, String searchEngine){
		BasicDBObject dbquery = new BasicDBObject();
		String query = SearchProvider.constructQuery(seedList, concept);
        dbquery.put("query", query);
        DBCursor cur = searchCollection.find(dbquery);
    	Gson gson = new Gson();

        if(cur.count()>0){
        	System.out.println("@@@");
        	JsonParser p = new JsonParser();
        	SearchResult result = gson.fromJson(p.parse(cur.next().toString()), SearchResult.class);
        	return result.getResults();
        }else{
        	SearchProvider sp = new SearchProvider();
        	ArrayList<WebPage> result = sp.search(seedList, concept, noOfResults, overlapTolerance, searchEngine);
        	LogUtil.log.info("Going to Search Engine : " + searchEngine +" for " + query + " got "+ result.size() + " results");
        	SearchResult sr = new SearchResult(query,result);
    		DBObject obj = (DBObject)JSON.parse(gson.toJson(sr));
    		searchCollection.save(obj);
        	return result;
        }
	}
	
	private ArrayList<WebPage> search(ArrayList<String> seedList, String concept, int noOfResults, double overlapTolerance, String searchEngine)  {
		String query=constructQuery(seedList, concept);
		ArrayList<WebPage> pageLists = new ArrayList<>();
		switch(searchEngine){
		case CommonConstants.BING_SEARCH_ENGINE:
			pageLists= BingAPI.bingSearch(seedList, concept, noOfResults, overlapTolerance, query);
			break;
		case CommonConstants.GOOGLE_SEARCH_ENGINE:
			pageLists= GoogleAPI.googleSearch(seedList, concept, noOfResults, overlapTolerance, query);
			break;
		case CommonConstants.FAROO_SEARCH_ENGINE:
			pageLists= BingAPI.bingSearch(seedList, concept, noOfResults, overlapTolerance, query);
			break;
		case CommonConstants.TWITTER_SEARCH_ENGINE:
			pageLists= BingAPI.bingSearch(seedList, concept, noOfResults, overlapTolerance, query);
			break;
		case CommonConstants.YANDEX_SEARCH_ENGINE:
			pageLists= BingAPI.bingSearch(seedList, concept, noOfResults, overlapTolerance, query);
			break;
		default:
			pageLists = new ArrayList<>();
			break;
		}
		return pageLists;
	}
	
	public static ArrayList<String> getUrls(ArrayList<String> seedList, String concept, int noOfResults, double overlapTolerance, String searchEngine) {
		ArrayList<String> listURL = new ArrayList<String>();
		ArrayList<WebPage> results = getSearchResults(seedList, concept, noOfResults, overlapTolerance, searchEngine);
		for(WebPage r : results){
			listURL.add(r.getUrl());
		}
		return listURL;
	}
	
	private static String constructQuery(ArrayList<String> seedList, String concept) {
		String ret = new String();
		for(String s : seedList){
			ret = ret + " " + s;
		}
		return concept==null ? ret : " \"List of "+concept + "\""+ret;
	}	
	
	public String getPageHtml(String url){
		BasicDBObject query = new BasicDBObject();
        query.put("url", url);
        DBCursor cur = webCollection.find(query);
        
        if(cur.count()>0){
        	return (String)cur.next().get("html");
        }else{
        	String html = getHtml(url);
        	insert(url,html);
        	return html;
        }
	}
	
	private String getHtml(String url){
		LogUtil.log.fine("Going to web for : "+url );
		BufferedReader in;
		String inputLine;
		StringBuilder sb = new StringBuilder();
		try {
			in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
			while ((inputLine = in.readLine()) != null){
				sb.append(inputLine).append("\n");
			}
		} catch (MalformedURLException e) {
			LogUtil.log.fine(e.toString());
		} catch (IOException e) {
			LogUtil.log.fine(e.toString());
		}
		return sb.toString();
	}
	
	private void insert(String url, String html){
		BasicDBObject obj = new BasicDBObject("url",url).append("html", html);
		webCollection.insert(obj);
	}	
}
