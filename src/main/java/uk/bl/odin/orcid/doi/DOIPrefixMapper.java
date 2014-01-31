package uk.bl.odin.orcid.doi;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.google.appengine.repackaged.com.google.common.collect.Maps;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

@Singleton
public class DOIPrefixMapper {

	private final ImmutableMap<String, String> publisherMap;

	@Inject
	public DOIPrefixMapper() {
		publisherMap = ImmutableMap.copyOf(loadMap("doi-prefix-publishers.csv"));
	}

	// read the map cols1&2 doi->name
	private Map<String, String> loadMap(String file) {
		Map<String, String> temp = Maps.newHashMap();
		CsvMapper mapper = new CsvMapper();
		mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
		try {
			MappingIterator<Object[]> it = mapper.reader(Object[].class).readValues(
					getClass().getResourceAsStream(file));
			while (it.hasNext()) {
				Object[] row = it.next();
				if (row.length > 1 && (row[0] != null && row[1] != null)
						&& (!row[0].toString().isEmpty() && !row[1].toString().isEmpty())) {
					temp.put(row[0].toString().trim(), row[1].toString().trim());
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return temp;
	}

	/**
	 * A map of DOI prefixes -> Publisher name
	 * 
	 * Taken from https://gist.github.com/TomDemeranville/8699224 
	 * via https://gist.github.com/hubgit/5974843 
	 * via http://www.crossref.org/06members/51depositor.html
	 * 
	 * @return Sorted by doi
	 */
	public ImmutableMap<String, String> getPublisherMap() {
		return publisherMap;
	}
	
	/**
	 * A map of Publisher name -> DOI prefixes
	 * 
	 * @return sorted by publisher name
	 */
	public ImmutableMultimap<String, String> getPublisherMapInverse() {
		return publisherMap.asMultimap().inverse();
	}
	
	//todo: http://www.crossref.org/xref/xml/mddb.xml
	
	/** Util to sort a map by value.
	 * With thanks to http://stackoverflow.com/users/309596/carter-page
	 * 
	 */
	public static class MapUtil
	{
	    public static <K, V extends Comparable<? super V>> Map<K, V> 
	        sortByValue( Map<K, V> map )
	    {
	        List<Map.Entry<K, V>> list =
	            new LinkedList<Map.Entry<K, V>>( map.entrySet() );
	        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
	        {
	            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
	            {
	                return (o1.getValue()).compareTo( o2.getValue() );
	            }
	        } );

	        Map<K, V> result = new LinkedHashMap<K, V>();
	        for (Map.Entry<K, V> entry : list)
	        {
	            result.put( entry.getKey(), entry.getValue() );
	        }
	        return result;
	    }
	}

}
