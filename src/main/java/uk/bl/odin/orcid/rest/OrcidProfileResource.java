package uk.bl.odin.orcid.rest;

import java.io.IOException;

import javax.inject.Inject;

import org.restlet.data.Status;
import org.restlet.ext.jaxb.JaxbRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import uk.bl.odin.orcid.client.OrcidPublicClient;
import uk.bl.odin.orcid.guice.SelfInjectingServerResource;
import uk.bl.odin.orcid.schema.messages.onepointone.OrcidProfile;

/** Fetches orcid profiles and passes them through
 * 
 */
public class OrcidProfileResource extends SelfInjectingServerResource{

	@Inject
	OrcidPublicClient client;
	
	@Get("xml")
	public Representation getProfile(){
		try {
			OrcidProfile profile = client.getOrcidProfile(this.getAttribute("orcid"));
			return new JaxbRepresentation<OrcidProfile>(profile);
		}  catch (ResourceException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "invalid orcid, cannot resolve orcid");
		} catch (IOException e) {
			this.setStatus(Status.SERVER_ERROR_BAD_GATEWAY, e.getMessage());
		}		
		return null;
	}

}
