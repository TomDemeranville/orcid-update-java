package uk.bl.odin.orcid.rest;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.restlet.data.Status;
import org.restlet.ext.jaxb.JaxbRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import uk.bl.odin.orcid.client.OrcidPublicClient;
import uk.bl.odin.orcid.client.SearchKey;
import uk.bl.odin.orcid.client.constants.OrcidExternalIdentifierType;
import uk.bl.odin.orcid.client.constants.OrcidSearchField;
import uk.bl.odin.orcid.guice.SelfInjectingServerResource;
import uk.bl.odin.orcid.schema.messages.onepointone.OrcidSearchResults;

public class OrcidSearchResource extends SelfInjectingServerResource {

	private static final Logger log = Logger.getLogger(OrcidSearchResource.class.getName());
	
	@Inject
	OrcidPublicClient client;

	private SearchKey search = new SearchKey();

	/** Performs a search against the public API.
	 * Expects GET params:
	 * idtype - one of OrcidExternalIdentifierType, for example: isbn, doi, other-id
	 * term - the search term, 
	 * searchtype - one of 'exact', 'solr' or 'prefix'
	 * Optional params:
	 * page (from 0), pagesize
	 * 
	 */
	@Override
	public void doInit() {
		super.doInit();//for injection!
		try {
			OrcidSearchField field = OrcidExternalIdentifierType.fromString(this.getQueryValue("idtype"))
					.toOrcidSearchField();
			String searchTerm = this.getQueryValue("term");
			if (searchTerm == null)
				throw new IllegalArgumentException("missing term parameter");
			String searchType = this.getQueryValue("searchtype");
			if (searchType == null || !(searchType.equals("exact") || searchType.equals("prefix")))
				throw new IllegalArgumentException("invalid search type.  Pick one of 'exact', 'solr' or 'prefix'");
			if (searchType.equals("exact"))
				search.setQuery(field.buildExactQuery(searchTerm));
			else if (searchType.equals("prefix"))
				search.setQuery(field.buildPrefixQuery(searchTerm));
			else if (searchType.equals("solr"))
				search.setQuery(field.buildSolrQuery(searchTerm));

			String page = this.getQueryValue("page");
			String pageSize = this.getQueryValue("pagesize");
			if (page != null)
				search.setPage(Integer.parseInt(page));
			if (pageSize != null)
				search.setPagesize(Integer.parseInt(pageSize));
			
		} catch (IllegalArgumentException e) {
			this.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		}
	}

	@Get("xml")
	public Representation search() {
		try {
			OrcidSearchResults result = client.search(search.getQuery(), search.getPage(), search.getPagesize());
			return new JaxbRepresentation<OrcidSearchResults>(result);
		} catch (IOException e) {
			this.setStatus(Status.SERVER_ERROR_BAD_GATEWAY, e.getMessage());
			return null;
		} 
	}
	
}
