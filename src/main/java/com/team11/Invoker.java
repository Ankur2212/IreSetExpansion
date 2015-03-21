package com.team11;

import java.util.Set;

import com.team11.searchAPI.BingAPI;
import com.team11.searchAPI.GoogleAPI;

public class Invoker {
	public static void main(String[] args) {
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
		FarooAPI faroo = new FarooAPI();
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

	}
}
