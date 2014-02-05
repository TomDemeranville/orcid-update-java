package uk.bl.odin.orcid.doi;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/** Not ideal but reasonable first pass implementation of name->prefix mappings provider
 * 
 * TODO: for datacite we can
	get http://search.datacite.org/list/datacentres for list of names
	get http://search.datacite.org/list/prefixes for a list of prefixes
	get http://search.datacite.org/list/prefixes?fq=datacentre_symbol:TIB.GFZ&facet.mincount=1 to match them up.
	
	Or parse whole dump from: curl "http://search.datacite.org/api?q=prefix:*&fl=prefix,datacentre&wt=csv&csv.header=false&rows=99999999"
	which is datacite-all.json
 * TODO: for crossref we can parse http://www.crossref.org/xref/xml/mddb.xml
 * @author tom
 *
 */
@Singleton
public class DOIPrefixMapper {

	//name -> doi list
	private final ImmutableMultimap<String, String> publisherMap;
	private final ImmutableMultimap<String, String> datacentreMap;

	@Inject
	public DOIPrefixMapper() {
		publisherMap = loadPublisherMap("doi-prefix-publishers.csv");
		datacentreMap = loadBasicDatacentreMap("datacentre-prefixes.json");
	}
	
	private ImmutableMultimap<String, String> loadBasicDatacentreMap(String file){
		Multimap<String, String> m = LinkedHashMultimap.create();
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<DatacentrePrefixMapping> prefixes =  mapper.readValue(getClass().getResourceAsStream(file), new TypeReference<List<DatacentrePrefixMapping>>(){});
			for (DatacentrePrefixMapping mapping : prefixes){
				m.putAll(mapping.datacentre, mapping.prefixes);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return ImmutableMultimap.copyOf(m);
	}
	
	private ImmutableMultimap<String, String> loadPublisherMap(String file) {
		//todo make sortedsetmultimap
		Multimap<String, String> temp = LinkedHashMultimap.create();
		CsvMapper mapper = new CsvMapper();
		mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
		try {
			MappingIterator<Object[]> it = mapper.reader(Object[].class).readValues(
					getClass().getResourceAsStream(file));
			while (it.hasNext()) {
				Object[] row = it.next();
				if (row.length > 1 && (row[0] != null && row[1] != null)
						&& (!row[0].toString().isEmpty() && !row[1].toString().isEmpty())) {
					temp.put(row[1].toString().trim(), row[0].toString().trim());
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return ImmutableMultimap.copyOf(temp);
	}

	
	public ImmutableMultimap<String, String> getDatacentreMap() {
		return datacentreMap;
	}
	
	/**
	 * A map of Publisher name -> DOI prefixes
	 * 
	 * @return sorted by publisher name
	 */
	public ImmutableMultimap<String, String> getPublisherMap() {
		return publisherMap;
	}
	
	public static class DatacentrePrefixMapping{
		public String datacentre;
		public List<String> prefixes;
	}

	
	
}
