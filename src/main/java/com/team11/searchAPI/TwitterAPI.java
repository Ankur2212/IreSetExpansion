package com.team11.searchAPI;

import twitter4j.*;

import java.util.List;

public class TwitterAPI {
	/**
	 * Usage: java twitter4j.examples.search.SearchTweets [query]
	 *
	 * @param args search query
	 */
	public static void main(String[] args) 
	{
		if (args.length < 1) 
		{
			System.out.println("java twitter4j.examples.search.SearchTweets [query]");
			System.exit(-1);
		}
		Twitter twitter = new TwitterFactory().getInstance();
		try 
		{
			String queryString = args[0];
			Query query =null;
			int count  = Integer.parseInt(args[1]);
			QueryResult result;
			do {
				query = new Query(queryString);
				result = twitter.search(query);
				List<Tweet> tweets = result.getTweets();
				for (Tweet tweet : tweets) 
				{
					System.out.println("@" + tweet.getFromUser() + " - " + tweet.getText());
					count++;
				}
			} while ((queryString = result.getQuery()) != null);
			System.out.println(count);
			System.exit(0);
		} catch (TwitterException te) 
		{
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
			System.exit(-1);
		}
	}
}