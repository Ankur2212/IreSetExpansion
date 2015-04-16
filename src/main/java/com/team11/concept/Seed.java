/* Class for expanding the given set of related words  */

package com.team11.concept;

import java.io.File;
import java.util.ArrayList;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.util.SerializationUtils;


import com.team11.CommonUtilities.IRUtil;
import com.team11.CommonUtilities.LogUtil;
import com.team11.Parser.WebPage;
import com.team11.webDB.SearchProvider;



public class Seed {

	private TokenizerFactory tokenizer =  new DefaultTokenizerFactory();;

	public ArrayList<String> expandSeed(ArrayList<String> seedList, int noOfResults, String searchEngine) throws Exception {
		SearchProvider sp = new SearchProvider();
		StringBuilder tempSeed = new StringBuilder();
		for(String seed : seedList){
			tempSeed.append(seed).append(" ");
		}
		do {
			
			StringBuilder tempHtml = new StringBuilder();
			ArrayList<WebPage> listpages = SearchProvider.getSearchResults(seedList, null, noOfResults, 1, searchEngine);  // Query the given seed list in the required search engine
			for(WebPage wp : listpages){
				tempHtml.append(sp.getHtml(wp.getUrl()));       // Get HTML page of all the returned webpages
			}
			LogUtil.log.info("Input Seed : "+tempSeed.toString());
			String newSeed = getNewSeed(tempSeed.toString(),tempHtml.toString(),seedList);
			if (newSeed != null && !newSeed.equals("")){
				tempSeed.append(newSeed).append(" ");
				seedList.add(newSeed);
			}
			LogUtil.log.info("New Seed :  "+tempSeed.toString());
		}while(seedList.size() < 10);
		return seedList;
	}

	public String getNewSeed(String oldSeedString, String pageHtml,ArrayList<String> seedList){
		Word2Vec vec = SerializationUtils.readObject(new File("vec2.ser"));
		double weightage= 0.0;
		String newSeed = "";
		Tokenizer tf = tokenizer.create(pageHtml);   // tokenize the overall HTML into set of words
		while(tf.hasMoreTokens()){
			String htmlToken = tf.nextToken().toLowerCase();    // convert each word to lower case
			if(IRUtil.isValidWord(htmlToken)){					// Check for stop word
				htmlToken=IRUtil.getStemmedWord(htmlToken);		// Perform Stemming
				if (!seedList.contains(htmlToken)){				// Check if word is present in current seed list
					double temp = vec.similarity(oldSeedString, htmlToken);
					if (weightage< temp ){
						newSeed = htmlToken;					// finding a word which is not already in the seed list and has the highest similarity
						weightage = temp;
					}
				}
			}
		}
		return newSeed;										// return the new seed
	}
}
