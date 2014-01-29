package uk.bl.odin.orcid.rest.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import uk.bl.odin.orcid.client.OrcidPublicClient;
import uk.bl.odin.orcid.client.SearchKey;
import uk.bl.odin.orcid.client.SearchType;
import uk.bl.odin.orcid.client.constants.OrcidExternalIdentifierType;
import uk.bl.odin.orcid.client.constants.OrcidSearchField;
import uk.bl.odin.orcid.domain.CacheManager;
import uk.bl.odin.orcid.guice.SelfInjectingServerResource;
import uk.bl.odin.orcid.schema.messages.onepointone.OrcidSearchResult;
import uk.bl.odin.orcid.schema.messages.onepointone.OrcidSearchResults;

import com.google.common.base.Joiner;

/** Resource that compiles reports for datacentres.  
 * The reports contain a list of DOIs/Other identifiers the datacentre owns and the corresponding ORCiDs.
 * These reports have to be looked up one ORCiD at a time so can be SLOW to compile.
 * 
 * Initial report contains number of ORCiDs+names.  
 * 
 * A background task then fires in the browser to fetch all of those ORCiD records in order
 * to bring back the exact DOIs/Identifiers matched. This uses OrcidProfileResource.  We cannot compile this
 * server side on GAE due to timeout limitations.
 * 
 * TODO: urgh.  WHY U SO ANNOYING ORCID.  I'd love it if this query were built in.
 * TODO: this is a nightmare on GAE as we cannot run background tasks without paying money.
 * 
 * Handles request/response in Datatable format.  @see datatables.net
 * 
 * 
 * FETCH ALL WORKS ATTACHED TO ALL RESULTS.
 * 
 * 
 * @author tom
 *
 */
public class OrcidDataCentreReportResource extends SelfInjectingServerResource{
	
	private static final Logger log = Logger.getLogger(OrcidDataCentreReportResource.class.getName());

	@Inject
	OrcidPublicClient client;

	@Inject
	CacheManager cache;
	
	SearchKey search = new SearchKey();
	String sEcho;
	
	/** Parse the query.
	 * Expects datatable query params:
	 * iDisplayStart
	 * iDisplayLength
	 * sSearch
	 * sEcho
	 * 
	 * also optionally expects
	 * idtype (defaults to doi)
	 * searchtype (defaults to prefix)
	 * 
	 * @see http://datatables.net/usage/server-side
	 */
	@Override
	public void doInit() {
		super.doInit();//for injection!
		String iDisplayStart = this.getQueryValue("iDisplayStart");
		String iDisplayLength = this.getQueryValue("iDisplayLength");
		String sSearch = this.getQueryValue("sSearch");
		sEcho = this.getQueryValue("sEcho");
		
		OrcidSearchField field = OrcidSearchField.DIGITAL_OBJECT_IDS;
		if (this.getQueryValue("idtype") != null){
			try{
				field = OrcidExternalIdentifierType.fromString(getQueryValue("idtype")).toOrcidSearchField();
			}catch(IllegalArgumentException e){
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "invalid idtype");			
			}
		}
		
		SearchType type = SearchType.PREFIX;
		if (getQueryValue("searchype")!=null){
			try{
				type = SearchType.fromString(getQueryValue("searchtype"));
			}catch(IllegalArgumentException e){
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "invalid searchtype");			
			}
		}
		
		search.setQuery(field.buildQuery(type, sSearch));
		search.setPage(Integer.parseInt(iDisplayStart));
		search.setPagesize(Integer.parseInt(iDisplayLength));
	}

	/** Get a datatable representation of the search results. 
	 * Suitable for use with http://datatables.net/
	 *  Caches results with provided CacheManager
	 */
	@Get("json")
	public Representation getDataTableRepresentaion() {		
		try {
			OrcidSearchResults searchResults = cache.getSearchCache().getIfPresent(search);
			if (searchResults==null)
				searchResults = client.search(search.getQuery(), search.getPage(), search.getPagesize());
			cache.getSearchCache().put(search, searchResults);
			DatatableResults list = new DatatableResults();
			list.iTotalRecords=searchResults.getNumFound().intValue();
			list.iTotalDisplayRecords = searchResults.getNumFound().intValue();
			list.sEcho = Integer.parseInt(sEcho);
			Joiner joiner = Joiner.on(", ").skipNulls();
			for (OrcidSearchResult result : searchResults.getOrcidSearchResult()){
				BriefResult briefResult = new BriefResult();
				briefResult.orcid=result.getOrcidProfile().getOrcidIdentifier().getPath();
				briefResult.link="<a href='"+result.getOrcidProfile().getOrcidIdentifier().getUri()+"' target='_blank'>"+briefResult.orcid+"</a>";
				briefResult.name= joiner.join(
						result.getOrcidProfile().getOrcidBio().getPersonalDetails().getFamilyName(),
						result.getOrcidProfile().getOrcidBio().getPersonalDetails().getGivenNames());
				list.aaData.add(briefResult);
			}
			return new JacksonRepresentation<DatatableResults>(list);
		} catch (IOException e) {
			this.setStatus(Status.SERVER_ERROR_BAD_GATEWAY, e.getMessage());
			return null;
		} 
	}
	
	//http://datatables.net/release-datatables/examples/data_sources/server_side.html
	//http://datatables.net/release-datatables/examples/ajax/objects.html
	
	//this one
	//http://datatables.net/forums/discussion/16675/twitter-bootstrap-3
	public static class DatatableResults{
		public List<BriefResult> aaData= new ArrayList<BriefResult>();
		public int iTotalRecords = 0;
		public int iTotalDisplayRecords = 0;
		public int sEcho = 0;
	}
	
	public static class BriefResult{
		public String name;
		public String orcid;
		public String link;
	}

}
