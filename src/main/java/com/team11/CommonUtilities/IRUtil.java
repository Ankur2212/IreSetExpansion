/*

Utility for parsing and tokenizing

*/

package com.team11.CommonUtilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import com.team11.concept.Stemmer;


public class IRUtil {
	private static int totalDocs=0;
	public static final String UrlRegEx = " |\\?|\\.|/|:|-|\\+|%|=|&|\\$|,|_|;|\\(|\\)|\\{|\\}|\\[|\\]|&|%";  // Regex for detecting URLs
	public static final String Token = " |\\?|\\.|/|:|\\+|%|=|&|\n|\\$|,|_|;|\\(|\\)|\\{|\\}|\\[|\\]|&|%";   // Regex for tokenizing words
	static HashSet<String> stopWords = new HashSet<String>();

	static {
		BufferedReader reader=null;
		try{
			reader = new BufferedReader(new FileReader("stopwords"));
			stopWords.addAll( Arrays.asList(reader.readLine().split(",")));				// Read file containing stop words
			reader.close();
			LogUtil.log.info("No of StopWords :"+stopWords.size());
		} catch (FileNotFoundException e) {
			System.err.println("stopwords File Not found");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(reader != null)
				try{
					reader.close();
				}catch (Exception e) {
					e.printStackTrace();
				}
		}
	}


	public static boolean isValidWord(String smallWord) {
		//Matcher matcher = nonWordPattern.matcher(smallWord);

		//if(matcher.find() || stopWords.contains(smallWord) || smallWord.length()<2 ){
		if(smallWord.length()<=2 || stopWords.contains(smallWord)){								 // Consider words of length greater than 2 and are not stop words					
			//System.out.println("This is Invalid Concept (stopword or not-word) : " + word);
			return false;
		}
		return true;
	}

	public static ArrayList<String> getUrltokens(String urlString){     
		ArrayList<String> tokens = new ArrayList<String>();
		URI url = null;
		try {
			try {
				url = new URI(URLEncoder.encode(urlString, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(url.getPath()!=null){
			tokens.addAll(Arrays.asList(url.getPath().split(IRUtil.UrlRegEx)));     // Get URL path using the URL regex
		}
		if(url.getQuery()!=null){
			tokens.addAll(Arrays.asList(url.getQuery().split(IRUtil.UrlRegEx)));     // Get the query from the URL
		}
		return tokens;
	}

	public static String getStemmedWord(String word){
		Stemmer stemmer = new Stemmer();
		stemmer.add(word.toLowerCase().toCharArray(), word.length());				// Perform stemming on the given word
		stemmer.stem();
		return stemmer.toString();
	}
}
