package com.team11;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.team11.CommonUtilities.LogUtil;
import com.team11.concept.Seed;
import com.team11.concept.Word2VecTraining;

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
		String searchEngine=CommonConstants.BING_SEARCH_ENGINE;
		//String searchEngine=CommonConstants.GOOGLE_SEARCH_ENGINE;

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
				try {
					new Word2VecTraining().word2VecTraining();
					ArrayList<String> list = seed.expandSeed(seedList,noOfResults,searchEngine);
					StringBuilder sb = new StringBuilder();
					for (String s : list){
						sb.append(s);
						sb.append("\t");
					}
					writer.append(sb.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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

}
