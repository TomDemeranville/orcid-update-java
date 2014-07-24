package uk.bl.odin.orcid.htmlmeta;

import static org.junit.Assert.*;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.repackaged.com.google.common.collect.Sets;

import uk.bl.odin.orcid.domain.IsOrcidWork;
import uk.bl.odin.orcid.htmlmeta.dc.DC_KEYS;
import uk.bl.odin.orcid.htmlmeta.dc.DublinCoreMeta;
import uk.bl.odin.orcid.htmlmeta.highwire.HW_KEYS;
import uk.bl.odin.orcid.htmlmeta.highwire.HighwireMeta;

public class GenericHTMLMetaBuilderTest {

	@Test
	public final void test() throws IOException {

		Document doc = Jsoup.parse(getClass().getResourceAsStream("meta.html"),"UTF-8","");
		//Document doc = Jsoup.connect("").timeout(10000).get();
		HTMLMetaBuilder builder = new HTMLMetaBuilder(doc);		
		
		DublinCoreMeta dc = builder.getDublinCoreMeta();
		assertEquals(dc.get(DC_KEYS.CREATOR),Sets.newHashSet("Remy Durand", "Karl Deisseroth", "Jin Hyung Lee", "Viviana Gradinaru", "Dae-Shik Kim", "Lief E Fenno", "Inbal Goshen", "Feng Zhang", "Charu Ramakrishnan"));
		assertEquals(dc.get(DC_KEYS.IDENTIFIER),Sets.newHashSet("doi:10.1038/nature09108", "pmid:20473285"));
		assertEquals(dc.get(DC_KEYS.PUBLISHER),Sets.newHashSet("Nature Publishing Group"));
		assertEquals(dc.get(DC_KEYS.DATE),Sets.newHashSet("2010"));
		assertEquals(dc.get(DC_KEYS.TITLE),Sets.newHashSet("Global and local fMRI signals driven by neurons defined optogenetically by type and wiring."));
		
		HighwireMeta hw = builder.getHighwireMeta();
		assertEquals(hw.get(HW_KEYS.AUTHORS),Sets.newHashSet("Lee, Jin Hyung; Durand, Remy; Gradinaru, Viviana; Zhang, Feng; Goshen, Inbal; Kim, Dae-Shik; Fenno, Lief E; Ramakrishnan, Charu; Deisseroth, Karl"));
		assertEquals(hw.get(HW_KEYS.DOI),Sets.newHashSet("10.1038/nature09108"));
		assertEquals(dc.get(DC_KEYS.PUBLISHER),Sets.newHashSet("Nature Publishing Group"));
		assertEquals(dc.get(DC_KEYS.DATE),Sets.newHashSet("2010"));
		assertEquals(dc.get(DC_KEYS.TITLE),Sets.newHashSet("Global and local fMRI signals driven by neurons defined optogenetically by type and wiring."));
		//title, issue,volume,firstpage,lastpage
		//bibtex?
	}

}