package uk.bl.odin.orcid;

import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.resource.Directory;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;

import uk.bl.odin.orcid.domain.IsOrcidWorkProvider;
import uk.bl.odin.orcid.domain.OrcidOAuthClient;
import uk.bl.odin.orcid.rest.CacheFilter;
import uk.bl.odin.orcid.rest.MetadataFetchResource;
import uk.bl.odin.orcid.rest.OrcidAuthURLResource;
import uk.bl.odin.orcid.rest.OrcidTokenResource;
import uk.bl.odin.orcid.rest.OrcidWorkCreationResource;

public class RootRouter extends Router {

	private static final Logger log = Logger.getLogger(RootRouter.class.getName());

	public RootRouter(Context context) {
		super(context);

		// configure our resources

		// convert authz codes from orcid into authz tokens
		this.attach("/orcid/token", OrcidTokenResource.class);

		// generate a authz request url (?redirect=true to bounce user)
		this.attach("/orcid/requests/{originalRef}", OrcidAuthURLResource.class);
		this.attach("/orcid/requests", OrcidAuthURLResource.class);

		// create a work by posting orcid work XML
		this.attach("/orcid/{orcid}/orcid-works/create", OrcidWorkCreationResource.class);

		// fetch metadata from external source - use (?json) for raw form
		this.attach("/meta/{id}", MetadataFetchResource.class);

		// add a webjars listener.
		final Directory dir = new Directory(getContext(), "clap://class/META-INF/resources/webjars");
		Filter cache = new CacheFilter(getContext(), dir);
		this.attach("/webjars", cache);

		// configure our oauth client
		String clientID = this.getContext().getParameters().getFirst("OrcidClientID").getValue().toString();
		String clientSecret = this.getContext().getParameters().getFirst("OrcidClientSecret").getValue().toString();
		String returnURI = this.getContext().getParameters().getFirst("OrcidReturnURI").getValue().toString();
		boolean sandbox = Boolean.valueOf(this.getContext().getParameters().getFirst("OrcidSandbox").getValue()
				.toString());
		OrcidOAuthClient client = new OrcidOAuthClient(clientID, clientSecret, returnURI, sandbox);
		context.getAttributes().put("OrcidOAuthClient", client);

		// configure our metadata provider
		String providerName = this.getContext().getParameters().getFirst("OrcidWorkProvider").getValue().toString();
		try {
			IsOrcidWorkProvider orcidWorkProvider = (IsOrcidWorkProvider) Class.forName(providerName).newInstance();
			context.getAttributes().put("OrcidWorkProvider", orcidWorkProvider);
		} catch (Exception e) {
			log.severe("Cannot instatiate OrcidWorkProvider - make sure this implements IsOrcidWorkProvider, is set in the web.xml as an init param and has no-arg constructor "
					+ e);
		}
		log.info("RootRouter created, ready to serve");
	}

}
