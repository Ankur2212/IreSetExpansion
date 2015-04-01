package com.team11.CommonUtilities;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;

public class MapUtil {

	public static HashMap<String, Integer> updateFrequency(HashMap<String, Integer> map, String smallWord, int weight) {
		//String smallWord = getStemmedWord(word.toLowerCase());
		Integer freq = map.get(smallWord);
		if(freq == null){
			map.put(smallWord, weight);
		}else{
			map.put(smallWord, freq+weight);
		}
		return map;
	}

	public static String getMaxKey(HashMap<String, Integer> freqMap, int threshold) {
		int maxValue = 0;
		String maxKey =null;
		for(Entry<String, Integer> entry : freqMap.entrySet()){
			if(entry.getValue() > maxValue){
				maxValue=entry.getValue();
				maxKey=entry.getKey();
			}
		}
		//System.out.println("with count "+maxValue);
		return maxValue>=threshold ? maxKey : null;
	}

	public static Iterable<Entry<Double, String>> getTopKEntries(HashMap<String, Integer> freqMap, int k) {
		/*IterableByValuesMap sorByValuesMap = new IterableByValuesMap();
		for(Entry<String, Integer> e : freqMap.entrySet()){
			sorByValuesMap.put(e.getKey(),(double)e.getValue());
		}

		return sorByValuesMap.getTopKEntries(k);*/
		TreeMultimap<Double,String> sortedByValues = TreeMultimap.create(Ordering.natural().reverse(),Ordering.natural());
		for(Entry<String, Integer> e : freqMap.entrySet()){
			sortedByValues.put((double)e.getValue(),e.getKey());
		}
		return Iterables.limit(sortedByValues.entries(),k);
	}

	public static HashMap<String, Set<String>> updateDocFrequency(
			HashMap<String, Set<String>> docMap, String smallWord, String docUrl) {
		//String smallWord = getStemmedWord(word.toLowerCase());
		Set<String> value = docMap.get(smallWord);
		if(value == null) {
			value = new HashSet<String>();
		}
		value.add(docUrl);
		docMap.put(smallWord, value);
		return docMap;
	}

	public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
		Comparator<K> valueComparator =  new Comparator<K>() {
			public int compare(K k1, K k2) {
				int compare = map.get(k2).compareTo(map.get(k1));
				if (compare == 0) return 1;
				else return compare;
			}
		};
		Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
		sortedByValues.putAll(map);
		return sortedByValues;
	}
}
