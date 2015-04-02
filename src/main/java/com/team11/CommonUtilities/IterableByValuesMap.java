package src.main.java.com.team11.CommonUtilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;

public class IterableByValuesMap extends ForwardingMap<String,Double> implements Iterable<java.util.Map.Entry<Double,String>> {

	// This is the words->long Map
	private final Map<String,Double> hashMap = new HashMap<String,Double>();

	// This is the sorted-by-long group of words. Bonus: the words
	// are also sorted which is important for fast removal, and means that 
	// when you print it out, everything is sorted :)
	private final TreeMultimap<Double,String> sortedByValues = TreeMultimap.create(Ordering.natural().reverse(),Ordering.natural());

	@Override
	protected Map<String, Double> delegate() {    
		// this is so that most of the Map methods forward to the HashMap
		return hashMap;     
	}

	@Override
	public Double put(String word, Double newValue) {
		Double lastValue = hashMap.get(word);
		if(lastValue != null) {
			// this word was already in the map, so
			// we need to remove it from the sorted
			// values set.
			sortedByValues.remove(lastValue, word);
		}
		sortedByValues.put(newValue, word);
		hashMap.put(word, newValue);

		return lastValue;
	}

	@Override
	public Double remove(Object word) {
		Double lastValue = hashMap.remove(word);
		// we may have to remove this key from the sorted collection
		if(lastValue != null) sortedByValues.remove(lastValue,word);
		return lastValue;
	}

	@Override
	public void putAll(Map<? extends String,? extends Double> otherMap) {
		// so we have a putAll that works in terms of our put
		standardPutAll(otherMap);
	}

	@Override
	public void clear() { // this is so that clear works on both collections
		hashMap.clear();
		sortedByValues.clear();
	}

	public Iterator<Entry<Double,String>> iterator() {
		return sortedByValues.entries().iterator();
	}
	
	public Iterable<Entry<Double, String>> getTopKEntries(int k) {
		return Iterables.limit(sortedByValues.entries(),k);
	}

}