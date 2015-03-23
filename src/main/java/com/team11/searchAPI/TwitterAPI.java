package com.team11.searchAPI;
package twitter4j.examples.search;
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
		Query query = new Query(args[0]);
		int count  = int(args[1]);
		QueryResult result;
	do {
			result = twitter.search(query);
			List<Status> tweets = result.getTweets();
			for (Status tweet : tweets) 
			{
				System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
				count++;
			}
		} while ((query = result.nextQuery()) != null);
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