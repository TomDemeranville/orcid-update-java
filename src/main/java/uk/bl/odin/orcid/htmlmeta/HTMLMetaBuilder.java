package uk.bl.odin.orcid.htmlmeta;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import uk.bl.odin.orcid.htmlmeta.dc.DC_KEYS;
import uk.bl.odin.orcid.htmlmeta.dc.DublinCoreMeta;
import uk.bl.odin.orcid.htmlmeta.eprints.EP_KEYS;
import uk.bl.odin.orcid.htmlmeta.eprints.EPrintsMeta;
import uk.bl.odin.orcid.htmlmeta.highwire.HW_KEYS;
import uk.bl.odin.orcid.htmlmeta.highwire.HighwireMeta;
import uk.bl.odin.orcid.htmlmeta.prism.PrismMeta;

/**
 * Class that delegates to various HTML meta extractors and returns a best
 * matching composite view.
 * 
 * Supports the same meta as google scholar: Google Scholar supports Highwire
 * Press tags (e.g., citation_title), Eprints tags (e.g., eprints.title), BE
 * Press tags (e.g., bepress_citation_title), and PRISM tags (e.g.,
 * prism.title). Use Dublin Core tags (e.g., DC.title) as a last resort - they
 * work poorly for journal papers because Dublin Core doesn't have unambiguous
 * fields for journal title, volume, issue, and page numbers. To check that
 * these tags are present, visit several abstracts and view their HTML source.
 * 
 * THEY ALL NEED WORK - THEY'VE BEEN BUNGED TOGETHER QUICKLY
 * 
 * @see http://scholar.google.com/intl/en/scholar/inclusion.html#indexing
 * 
 * @author tom
 * 
 */
public class HTMLMetaBuilder {

	private DublinCoreMeta dc;
	private HighwireMeta hw;
	private PrismMeta pm;
	private EPrintsMeta ep;
	
	private Document htmldoc;
	
	public HTMLMetaBuilder(Document htmldoc) {
		this.htmldoc=htmldoc;
	}

	public DublinCoreMeta getDublinCoreMeta(){
		if (dc == null){
			dc = new DublinCoreMeta();
			//build dublin core
			for (DC_KEYS key : DC_KEYS.values()){
				Elements matching = htmldoc.select("meta[name="+DublinCoreMeta.DC_PREFIX+"."+key+"]");
				for (Element e: matching){
					String value = e.attr("content");
					if (!value.isEmpty())
						dc.put(key, value);				
				}
			}
			for (DC_KEYS key : DC_KEYS.values()){
				Elements matching = htmldoc.select("meta[name="+DublinCoreMeta.DCTERMS_PREFIX+"."+key+"]");
				for (Element e: matching){
					String value = e.attr("content");
					if (!value.isEmpty())
						dc.put(key, value);				
				}
			}
		}
		return dc;
	}
	
	public HighwireMeta getHighwireMeta(){
		if (hw == null){
			hw = new HighwireMeta();
			for (HW_KEYS key : HW_KEYS.values()){
				Elements matching = htmldoc.select("meta[name="+key+"]");
				for (Element e: matching){
					String value = e.attr("content");
					if (!value.isEmpty())
						hw.put(key, value);				
				}
			}
		}
		return hw;
	}
	
	public PrismMeta getPrismMeta(){
		if (pm == null){
			pm = new PrismMeta();
		}
		return pm;
	}

	public EPrintsMeta getEPrintsMeta(){
		if (ep == null){
			ep = new EPrintsMeta();
			for (EP_KEYS key : EP_KEYS.values()){
				Elements matching = htmldoc.select("meta[name="+key+"]");
				for (Element e: matching){
					String value = e.attr("content");
					if (!value.isEmpty())
						ep.put(key, value);				
				}
			}
		}
		return ep;
	}
}
