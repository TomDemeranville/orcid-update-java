package uk.bl.odin.orcid.rest;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import uk.bl.odin.orcid.domain.OrcidOAuthClient;
import uk.bl.odin.orcid.guice.SelfInjectingServerResource;

public class OrcidAuthURLResource extends SelfInjectingServerResource {

	@Inject OrcidOAuthClient orcidOAuthClient;
	
	/**
	 * Generates an authz request url to direct the user to ?redirect.true
	 * bounces user with a http redirect.
	 * 
	 * @return json {"url":"..."}
	 */
	@Get
	public Map<String, String> getAuthzCodeRedirectURL() {
		String ref = this.getAttribute("originalRef");
		String url = orcidOAuthClient.getAuthzCodeRequest(ref);
		if (this.getQueryValue("redirect") != null) {
			this.redirectPermanent(url);
			return null;
		}
		Map<String, String> m = new HashMap<String, String>();
		m.put("url", url);
		return m;
	}

}
