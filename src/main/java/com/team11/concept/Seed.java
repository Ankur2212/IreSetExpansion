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
			ArrayList<WebPage> listpages = SearchProvider.getSearchResults(seedList, null, noOfResults, 1, searchEngine); 
			for(WebPage wp : listpages){
				tempHtml.append(sp.getHtml(wp.getUrl()));
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
		Tokenizer tf = tokenizer.create(pageHtml);
		while(tf.hasMoreTokens()){
			String htmlToken = tf.nextToken().toLowerCase();
			if(IRUtil.isValidWord(htmlToken)){
				htmlToken=IRUtil.getStemmedWord(htmlToken);
				if (!seedList.contains(htmlToken)){
					double temp = vec.similarity(oldSeedString, htmlToken);
					if (weightage< temp ){
						newSeed = htmlToken;
						weightage = temp;
					}
				}
			}
		}
		return newSeed;
	}
}
