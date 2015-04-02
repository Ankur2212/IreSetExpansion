package src.main.java.com.team11.concept;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import src.main.java.com.team11.CommonUtilities.IRUtil;
import src.main.java.com.team11.CommonUtilities.LogUtil;
import src.main.java.com.team11.CommonUtilities.MapUtil;
import src.main.java.com.team11.webDB.SearchProvider;
import src.main.java.com.team11.Parser.WebList;
import src.main.java.com.team11.Parser.WebPage;

public class Seed {
	private static final double VARIANCE = 0.20;
	//private static final double OVERLAP_TOLERANCE = 0.4;
	//private static final int STOP_THRESHOLD = 4;
	static int fileCount = 1;
	final int seedThrehold = 10;
	static ArrayList<WebPage> seedPages = new ArrayList<WebPage>();
	static HashMap<String,Double> finalSeedScores = new HashMap<String, Double>();
	
	public ArrayList<String> expandSeed(ArrayList<String> seedList, int noOfResults, String searchEngine) throws IOException {
		finalSeedScores.clear(); // very imp

		// step 0 get the initial concept
		String concept= Extractor.getConcept(seedList,null,noOfResults,1,searchEngine);
		LogUtil.log.info("got initial concept as : " +concept);
		ArrayList<String> newSeedList = new ArrayList<String>(seedList);
		for(String seed : seedList){
			finalSeedScores.put(seed, 1.0);
		}
		do{
			seedList=newSeedList;
			//String newSeed = getNextSeed(seedList, concept, noOfResults,1/(1+Math.pow(Math.E,-iteration)));
			//System.out.println("aaaaa "+seedList.size());
			String newSeed = getNextSeed(seedList, concept, noOfResults,1,searchEngine);

			LogUtil.log.info(newSeed + " avgFinalSeedScore()="+avgFinalSeedScore() +" and seedScore="+finalSeedScores.get(newSeed));

			if(finalSeedScores.get(newSeed) >= avgFinalSeedScore()*VARIANCE){
				LogUtil.log.info("Adding the new Seed : "+newSeed);
				newSeedList.add(newSeed);
			}else{
				LogUtil.log.info("Stopping with "+seedList);
				break;
			}
			LogUtil.log.info("New Seed list is : "+newSeedList);
			// setp 3 extract the concept
			String candidateConcept = Extractor.getConcept(seedList,null,noOfResults,1,searchEngine);
			//if(Extractor.isValidConcept(newSeedList,noOfResults, candidateConcept)){
			concept=candidateConcept;
			//}
			LogUtil.log.info("got concept for "+newSeedList + " as : "+concept);
		}while(true);
		//System.out.println(seedList.size());
		return seedList;
	}

	private static Double avgFinalSeedScore() {
		double total=0;
		for(Double score :finalSeedScores.values()){
			total += score;
		}
		return total/finalSeedScores.size();
	}

	public String getNextSeed(ArrayList<String> seedList, String concept, int noOfResults, double overlapTolerance, String searchEngine){
		HashMap<String, Integer> freqMap = new HashMap<String, Integer>();
		HashMap<String,Set<String>> docMap = new HashMap<String, Set<String>>();
		ArrayList<WebPage> listPages = SearchProvider.getSearchResults(seedList,concept,noOfResults,overlapTolerance,searchEngine);
		LogUtil.log.fine("ListPages.size()" + listPages.size());
		seedPages.addAll(listPages); // mem or not ?
		//System.out.println(seedPages.size());
		for(WebPage wp : seedPages){
			//System.out.println("$$$ "+wp.getAllList().size());
			for(WebList wl : wp.getAllList()){
				//System.out.println("WebLists for "+wp.getUrl() + "\n" + wl.getList());
				for(String item : wl.getList()){		
					boolean isValid = Extractor.isValid(item,seedList);
					//System.out.println(isValid);
					if(isValid){
						MapUtil.updateFrequency(freqMap, item.toLowerCase(), 1);
						MapUtil.updateDocFrequency(docMap, item.toLowerCase(), wp.getUrl());
					}
				}
			}
		}
		//LogUtil.log.finer("freqMap in getNextSeed : " + freqMap);
		//LogUtil.log.finer("docMap in getNextSeed : " + docMap);


		LogUtil.log.fine("in getNextSeed() getting the best "+ 5 + " candidates : ");
		TreeMap<Double,String> seedScoreMap = new TreeMap<Double,String>();
		for(Entry<Double, String> en : MapUtil.getTopKEntries(freqMap, 5)){
			String candidateSeed =en.getValue();
			double s = getSeedScore(candidateSeed,seedList,noOfResults, searchEngine);
			//LogUtil.log.info("Trying out seed "+candidateSeed+" which had tf="+en.getKey()+": got score :" +s);
			seedScoreMap.put(s,candidateSeed);
		}
		//System.out.println(seedScoreMap.lastEntry());
		finalSeedScores.put(seedScoreMap.lastEntry().getValue(), seedScoreMap.lastEntry().getKey());

		return seedScoreMap.lastEntry().getValue();
	}

	private double getSeedScore(String candidateSeed, ArrayList<String> seedList, int noOfResults, String searchEngine) {
		ArrayList<String> mockSeedList = new ArrayList<String>();
		mockSeedList.add(candidateSeed);
		ArrayList<String> newSeed = SearchProvider.getUrls(mockSeedList, null, noOfResults,1,searchEngine);
		ArrayList<String> seeds = SearchProvider.getUrls(seedList, null, noOfResults,1,searchEngine);
		double score=0,temp=0;
		for(String c : newSeed){
			for(String s : seeds){
				temp=IRUtil.compareDocsWithSeed(c,s,seedList);
				LogUtil.log.fine(candidateSeed + " : "+ c +" : "+s+" = "+temp);
				score += temp;
			}
		}
		LogUtil.log.fine(seedList + " : " + candidateSeed + " scored = "+score);
		return score;
	}
}
