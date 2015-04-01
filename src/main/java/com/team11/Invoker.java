package com.team11;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.team11.CommonUtilities.LogUtil;
import com.team11.concept.Seed;

public class Invoker {
	/**
	 * @param args
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws IOException {
		//// INPUTS
		String inputFilePath=args[0];
		String outputFileName=args[1];
		int noOfResults = Integer.parseInt(args[2]);
		//String searchEngine=CommonConstants.BING_SEARCH_ENGINE;
		String searchEngine=CommonConstants.GOOGLE_SEARCH_ENGINE;

		////////////////////////////////
		BufferedReader reader = null;
		FileWriter writer = null;
		String line;
		ArrayList<String> seedList = new ArrayList<String>();
		Seed seed = new Seed();
		try {
			reader = new BufferedReader(new FileReader(inputFilePath));
			writer = new FileWriter(outputFileName);
			while ((line = reader.readLine()) != null && !line.equals("")) {
				LogUtil.log.info("======= "+line+" =======");
				seedList.addAll(Arrays.asList(line.toLowerCase().split(" ")));	
				writer.append(seed.expandSeed(seedList,noOfResults,searchEngine).toString());
			}
			reader.close();
			writer.close();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/*public static void main(String[] args) {
		String query="mario";
		int totalrecords=20;
				
		System.out.println("Google *******************************");
		GoogleAPI obj = new GoogleAPI();
		Set<String> result = obj.getDataFromGoogle(query,totalrecords);
		for(String temp : result){
			System.out.println(temp);
		}

		System.out.println("Bing *******************************");
		BingAPI bing = new BingAPI();
		Set<String> bingResult = bing.getDataFromBing(query,totalrecords);
		for(String temp : bingResult){
			System.out.println(temp);
		}

		System.out.println("Faroo *******************************");
		int count=0;
		FarooSearch faroo = new FarooSearch();
		Set<String> Farooresult = faroo.SearchFaroo(query,totalrecords);
		for(String temp : Farooresult){
			count++;
			System.out.println(temp);
			if(count==totalrecords) break;
		}

		System.out.println("Yandex *******************************");
		count=0;
		YandexAPI yandex = new YandexAPI();
		Set<String> Yandexresult = yandex.SearchYandex(query,totalrecords);
		for(String temp : Yandexresult){
			count++;
			System.out.println(temp);
			if(count==totalrecords) break;
		}

		System.out.println("Twitter *******************************");
		count=0;
		TwitterAPI twitter = new TwitterAPI();
	}*/

}
