package uk.bl.odin.orcid.htmlmeta;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import uk.bl.odin.orcid.domain.IsOrcidWork;
import uk.bl.odin.orcid.domain.IsOrcidWorkProvider;
import uk.bl.odin.orcid.ethos.EthosMetaScraper;
import uk.bl.odin.orcid.ethos.ThesisMetadata;

public class DelegatingMetaScraper implements IsOrcidWorkProvider {

	public static final Cache<String, HTMLMetaBuilder> cache = CacheBuilder.newBuilder()
				.expireAfterWrite(30, TimeUnit.MINUTES).maximumSize(100).build();
		
	@Override
	public IsOrcidWork fetch(String url) throws IOException {
		//check to see if we have an ethos ID
		System.out.println("here ");
		
		if (url.startsWith("uk.bl.ethos")){
			EthosMetaScraper scrape = new EthosMetaScraper();
			return scrape.fetch(url);
		}
		
		HTMLMetaBuilder builder = cache.getIfPresent(url);
		System.out.println("here 3");
		if (builder == null){
			System.out.println("looking up "+url);
			Document doc = Jsoup.connect(url).timeout(10000).get();
			builder = new HTMLMetaBuilder(doc);			
		}		
		
		return builder.getDublinCoreMeta();
		//return builder.getEPrintsMeta();	
	}

}
