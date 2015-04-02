package src.main.java.com.team11.concept;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import src.main.java.com.team11.CommonUtilities.IRUtil;
import src.main.java.com.team11.CommonUtilities.LogUtil;
import src.main.java.com.team11.webDB.SearchProvider;
import src.main.java.com.team11.Parser.WebList;
import src.main.java.com.team11.Parser.WebPage;

public class Extractor {
	static HashMap<String, Integer> freqMap = new HashMap<String, Integer>();
	static final Pattern nonWordPattern = Pattern.compile("[^\\w]");
	static HashMap<String,Set<String>> docMap = new HashMap<String, Set<String>>();

	public static String getConcept(ArrayList<String> seedList, String concept, int noOfResults, double overlapTolerance, String searchEngine) {
		//freqMap.clear();
		//docMap.clear();
		int weight=1;
		
		ArrayList<WebPage> listpages = SearchProvider.getSearchResults(seedList, null, noOfResults, overlapTolerance, searchEngine); 
		for(WebPage wp : listpages){
			// titles
			//Set<String> uniq = new HashSet<String>(Arrays.asList(arr));
			ArrayList<String> uniq = new ArrayList<String>(Arrays.asList(wp.getTitle().split(IRUtil.Token)));
			//weight--;
			update(uniq, seedList, weight, wp.getUrl());

			// snippest
			//Set<String> uniq = new HashSet<String>(Arrays.asList(arr));
			uniq = new ArrayList<String>(Arrays.asList( wp.getSnippest().split(IRUtil.Token)));
			update(uniq, seedList, weight, wp.getUrl());

			// urls
			//Set<String> uniq = new HashSet<String>(Arrays.asList(arr));
			uniq = new ArrayList<String>(IRUtil.getUrltokens(wp.getUrl()));
			update(uniq, seedList, weight, wp.getUrl());
			// sentences amd headings
			for(WebList wl : wp.getAllList()){				
				uniq = new ArrayList<String>(Arrays.asList(wl.getHeading().split(IRUtil.Token)));
				update(uniq, seedList, weight, wp.getUrl());

				uniq = new ArrayList<String>(Arrays.asList(wl.getDescription().split(IRUtil.Token)));
				update(uniq, seedList, weight, wp.getUrl());
			}
		}

		double score=0;
		TreeMap<Double, String> conceptMap = new TreeMap<Double, String>();
		for(Entry<String, Integer> e : freqMap.entrySet()){
			double tf = e.getValue();
			score=tf;
			conceptMap.put(score, e.getKey());
		}
		
		TreeMap<Double, String> conceptScoreMap = new TreeMap<Double, String>();
		int count=0;
		while(count++<3){
			Entry<Double, String> lastEntry =conceptMap.lastEntry();
			String candidateConcept = lastEntry.getValue();
			conceptMap.remove(lastEntry.getKey());
			double s = getConceptScore(candidateConcept,seedList,noOfResults,searchEngine);
			LogUtil.log.info("Trying out concept "+candidateConcept+" : got score :" +s);
			conceptScoreMap.put(s,candidateConcept);
		}

		LogUtil.log.fine("in Extract.extractConcept() Freq Map : "+ freqMap);
		LogUtil.log.fine("in Extract.extractConcept() Doc Map : "+ docMap);
		LogUtil.log.fine("in Extract.extractConcept() Score concpet : "+ conceptScoreMap);

		return conceptScoreMap.lastEntry().getValue();
	}

	private static double getConceptScore(String candidateConcept, ArrayList<String> seedList, int noOfResults, String searchEngine) {
		ArrayList<String> mockSeeds= new ArrayList<String>();
		mockSeeds.add(candidateConcept);
		ArrayList<String> concept = SearchProvider.getUrls(mockSeeds, null, noOfResults,1,searchEngine);
		ArrayList<String> seeds = SearchProvider.getUrls(seedList, null, noOfResults,1,searchEngine);
		double score=0,temp=0;
		for(String c : concept){
			for(String s : seeds){
				temp=IRUtil.compareDocs(c,s);
				LogUtil.log.fine(c +" : "+s+" = "+temp);
				score += temp;
			}
		}
		LogUtil.log.fine(seedList + " : " + candidateConcept + " scored = "+score);
		return score;
	}

	public static void update(ArrayList<String> uniq,ArrayList<String> seedList, int weight, String url){
		for(String word : uniq){
			if(isValid(word,seedList)){
				updateFrequency(word,weight);
				updateDocFrequency(word,url);
			}
		}
	}

	public static boolean isValid(String word, ArrayList<String> seedList) {
		String smallWord = word.toLowerCase();
		if(!IRUtil.isValidWord(smallWord)){
			LogUtil.log.finer("not a valid word : " + smallWord);
			return false;
		}

		for(String seed : seedList){
			if(seed.contains(smallWord) || smallWord.contains(seed)){
				LogUtil.log.finer("Word " + smallWord +" is already part of seed " + seed);
				return false;
			}
		}
		return true;
	}

	private static void updateFrequency(String word, int weight) {
		//String smallWord = getStemmedWord(word.toLowerCase());
		String smallWord = word.toLowerCase();
		//if(isValid(smallWord)){
		Integer freq = freqMap.get(smallWord);
		if(freq == null){
			freqMap.put(smallWord, weight);
		}else{
			freqMap.put(smallWord, freq+weight);
		}
		//}
	}

	private static void updateDocFrequency(String word, String docUrl) {
		//String smallWord = getStemmedWord(word.toLowerCase());
		String smallWord = word.toLowerCase();

		//if(isValid(smallWord)){
		Set<String> value = docMap.get(smallWord);
		if(value == null) {
			value = new HashSet<String>();
			//	}
			value.add(docUrl);
			docMap.put(smallWord, value);
		}
	}

	public static boolean isValidConcept(ArrayList<String> seedList,int noOfResults, String candidateConcept, String searchEngine) {
		double score=getConceptScore(candidateConcept, seedList,noOfResults,searchEngine);
		if(score/noOfResults > 20 ){
			return true;
		}else{
			return false;
		}
	}

}
