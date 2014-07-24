package uk.bl.odin.orcid.ethos;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import uk.bl.odin.orcid.domain.IsOrcidWorkProvider;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Simple scraper that extracts meta information from HTML pages fetched from
 * ethos.bl.uk.
 */
public class EthosMetaScraper implements IsOrcidWorkProvider {

	public static final String JSOUP_URL = "http://ethos.bl.uk/OrderDetails.do?uin=";
	// cache results for 30 minutes.
	public static final Cache<String, ThesisMetadata> cache = CacheBuilder.newBuilder()
			.expireAfterWrite(30, TimeUnit.MINUTES).maximumSize(100).build();

	// TODO:
	// DC.identifier not always present.
	// sometimes it's <meta name="citation_abstract_html_url"
	// content="http://hdl.handle.net/2381/8951" />
	// <meta name="DC.identifier" content="http://hdl.handle.net/2381/8951" />

	/**
	 * Scrape the DC metadata from the ETHOS HTML result.
	 * 
	 * @param ethosID
	 * @return a populated OrcidWorkMetadata
	 * @throws IOException
	 *             if JSOUP fails to retrieve document
	 */
	public ThesisMetadata fetch(String ethosID) throws IOException {

		ThesisMetadata meta = cache.getIfPresent(ethosID);
		if (meta != null)
			return meta;
		else
			meta = new ThesisMetadata();

		String url = JSOUP_URL + ethosID;
		Document doc = Jsoup.connect(url).timeout(10000).get();

		String creator = doc.select("meta[name=DC.creator]").first().attr("content").toString();
		String publisher = doc.select("meta[name=DC.publisher]").first().attr("content").toString();
		String title = doc.select("meta[name=DC.title]").first().attr("content").toString();
		String year = doc.select("meta[name=DCTERMS.issued]").first().attr("content").toString();
		String abstract_ = doc.select("meta[name=DCTERMS.abstract]").first().attr("content").toString();
		String id = doc.select("meta[name=DC.identifier]").first().attr("content").toString();
		String thesisType = doc.select("meta[name=thesis_type]").first().attr("content").toString();

		meta.setAbstract(abstract_);
		meta.setCreator(creator);
		meta.setPublisher(publisher);
		meta.setTitle(title);
		meta.setUrl(url);
		meta.setYear(year);
		meta.setThesisType(thesisType);// could use DC, but it's often
										// multi-valued.
		meta.getIdentifiers().add(ethosID);
		if (id != null && !id.isEmpty())
			meta.getIdentifiers().add(id);

		cache.put(ethosID, meta);
		return meta;
	}

}
