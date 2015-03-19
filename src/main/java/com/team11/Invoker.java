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

	}
}
