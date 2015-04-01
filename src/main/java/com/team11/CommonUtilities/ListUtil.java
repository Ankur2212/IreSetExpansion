package com.team11.CommonUtilities;



public class ListUtil {

	public static int getOverLap(Iterable<String> mylist, Iterable<String> seedList) {
		int count =0;
		for(String seed : seedList){
			for(String item : mylist){
				if(item.toLowerCase().contains(seed) || seed.toLowerCase().contains(item)){
					count++;
					break;
				}
			}
		}
		//System.out.println(count);
		return count;
	}

	public static double getOverLapWithoutStopWords(Iterable<String> list1, Iterable<String> list2) {
		double count =0;
		//System.out.println("List1 : "+ list1);
		//System.out.println("List2 : "+ list2);
		for(String w1 : list1){
			if(IRUtil.isValidWord(w1)){
				for(String w2 : list2){
					if(IRUtil.isValidWord(w2)){
						//if(w2.contains(w1) || w1.contains(w2)){ count++;break;}
						count+=StringUtil.compareStrings(w1, w2);
					}
				}
			}
		}
		return count;
	}

}
