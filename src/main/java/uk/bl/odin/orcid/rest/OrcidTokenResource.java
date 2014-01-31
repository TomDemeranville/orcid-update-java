package uk.bl.odin.orcid.rest;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import uk.bl.odin.orcid.client.OrcidAccessToken;
import uk.bl.odin.orcid.client.OrcidOAuthClient;
import uk.bl.odin.orcid.guice.SelfInjectingServerResource;

public class OrcidTokenResource extends SelfInjectingServerResource {

	private static final Logger log = Logger.getLogger(OrcidTokenResource.class.getName());
	
	@Inject
	OrcidOAuthClient orcidOAuthClient;

	/**
	 * This resource is hit by the return from a ORCID OAuth. returns Access
	 * token to client along with state param.
	 */
	@Get("json")
	public OrcidAccessToken getTokenResponse() {
		String code = this.getQueryValue("code");
		try {
			OrcidAccessToken token = orcidOAuthClient.getAccessToken(code);
			String ref = this.getQueryValue("state");
			token.setState(ref);
			return token;
		} catch (ResourceException e) {
			if (e.getStatus().isServerError())
				this.setStatus(Status.SERVER_ERROR_BAD_GATEWAY,e);
			else
				this.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e);
			log.info("Resource exception"+e.getMessage());
		} catch (IOException e) {
			this.setStatus(Status.SERVER_ERROR_BAD_GATEWAY, e);
		} 
		return null;
	}
}
