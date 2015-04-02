package src.main.java.com.team11.searchAPI;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import src.main.java.com.team11.CommonConstants;


public class YandexAPI implements CommonConstants{
	public Set<String> SearchYandex(String query,int records){
		int starting=0;
		Set<String> result=new HashSet<String>();
		Set<String> finalresult=new HashSet<String>();
		URL url;
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.iiit.ac.in", 8080));
		HttpURLConnection conn=null;
		BufferedReader br=null;
		String YandexKey=CommonConstants.arshYandexKey;
		try{
			while(starting<records){
				String start=Integer.toString(starting);
				url=new URL("https://xmlsearch.yandex.com/xmlsearch?l10n=en&user=arsh-anand&key="+YandexKey+"&query="+query+"&page="+start);
				//conn=(HttpURLConnection) url.openConnection(proxy);
				//conn.setRequestMethod("GET");
				
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(url.openConnection(proxy).getInputStream());
				NodeList nList = doc.getElementsByTagName("group");
				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						 Element eElement = (Element) nNode;
						 String resp=eElement.getElementsByTagName("url").item(0).getTextContent();
						 finalresult.add(resp);
					}
				}
				starting+=10;
			}
		}catch(Exception E){
			System.out.println("Error");
		}
		return finalresult;
	}
	
}

