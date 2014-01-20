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

/** RESTlet routing and general application configuration.
 */
public class RootRouter extends Router {

	private static final Logger log = Logger.getLogger(RootRouter.class.getName());

	/** Configures endpoints, sets up OrcidOAuthClient & OrcidWorkProvider and places them in the RESTlet Context
	 * 
	 * Routes:
	 * <ul>
	 * <li>"/orcid/token" convert authz codes from orcid into authz tokens </li>
	 * <li>"/orcid/requests" generate a authz request url (?redirect=true to bounce user) </li>
	 * <li>"/orcid/requests/{originalRef}" generate a authz request url with originalRef as state param (?redirect=true to bounce user) </li>
	 * <li>"/orcid/{orcid}/orcid-works/create" create a work by posting OrcidWork XML (requires ?token= orcid oauth token) </li>
	 * <li>"/meta/{id}" fetch metadata from external source - use (?json) for raw form </li>
	 * <li>"/webjars" webjars endpoint - example: /webjars/bootstrap/3.0.3/css/bootstrap.min.css </li>
	 * </ul>
	 * 
	 * Init params
	 * <ul>
	 * <li>"OrcidWorkProvider" fully qualified class name for IsOrcidWorkProvider instance</li>
	 * <li>"OrcidClientID", "OrcidClientSecret", "OrcidReturnURI" ORCID OAuth params</li>
	 * <li>"OrcidSandbox" true for sandbox, otherwise use live api</li>
	 * </ul>
	 * 
	 */
	public RootRouter(Context context) {
		super(context);

		//rest routes
		this.attach("/orcid/token", OrcidTokenResource.class);
		this.attach("/orcid/requests/{originalRef}", OrcidAuthURLResource.class);
		this.attach("/orcid/requests", OrcidAuthURLResource.class);
		this.attach("/orcid/{orcid}/orcid-works/create", OrcidWorkCreationResource.class);
		this.attach("/meta/{id}", MetadataFetchResource.class);

		// add a webjars listener (see http://demeranville.com/controlling-the-cache-headers-for-a-restlet-directory/ )
		final Directory dir = new Directory(getContext(), "clap://class/META-INF/resources/webjars");
		Filter cache = new CacheFilter(getContext(), dir);
		this.attach("/webjars", cache);

		// configure our oauth client
		if (!(context.getParameters().contains("OrcidClientID") &&
				context.getParameters().contains("OrcidClientSecret") &&
				context.getParameters().contains("OrcidReturnURI") &&
				context.getParameters().contains("OrcidSandbox") )){
			throw new IllegalStateException("cannot create OrcidOAuthClient - missing init parameter");
		}else{
			String clientID = context.getParameters().getFirst("OrcidClientID").getValue().toString();
			String clientSecret = context.getParameters().getFirst("OrcidClientSecret").getValue().toString();
			String returnURI = context.getParameters().getFirst("OrcidReturnURI").getValue().toString();
			boolean sandbox = Boolean.valueOf(context.getParameters().getFirst("OrcidSandbox").getValue()
					.toString());
			OrcidOAuthClient client = new OrcidOAuthClient(clientID, clientSecret, returnURI, sandbox);
			context.getAttributes().put("OrcidOAuthClient", client);			
		}
		
		// configure our metadata provider
		if (!context.getParameters().contains("OrcidWorkProvider")){
			throw new IllegalStateException("cannot create OrcidWorkProvier - missing init parameter");
		}else{
			String providerName = this.getContext().getParameters().getFirst("OrcidWorkProvider").getValue().toString();
			try {
				IsOrcidWorkProvider orcidWorkProvider = (IsOrcidWorkProvider) Class.forName(providerName).newInstance();
				context.getAttributes().put("OrcidWorkProvider", orcidWorkProvider);
			} catch (Exception e) {
				log.severe("Cannot instatiate OrcidWorkProvider - make sure this implements IsOrcidWorkProvider, is set in the web.xml as an init param and has no-arg constructor "
						+ e);
			}
		}
		log.info("RootRouter created, ready to serve");
	}

}
