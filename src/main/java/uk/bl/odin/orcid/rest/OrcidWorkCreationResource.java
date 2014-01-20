package uk.bl.odin.orcid.rest;

import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import uk.bl.odin.orcid.domain.OrcidOAuthClient;
import uk.bl.odin.schema.orcid.messages.onepointone.OrcidWork;

public class OrcidWorkCreationResource extends ServerResource {

	/** Accepts an XML encoded ORCID work. POSTs work to ORCID as a new work.
	 * Requires ?token= query param containing ORCID auth token.
	 * 
	 * @param rep
	 *            a serialised OrcidWork
	 * @throws IOException if we can't append. (TODO: handle properly)
	 */
	@Post
	public void addWork(Representation rep) throws IOException {
		try {
			JAXBContext jc = JAXBContext.newInstance(OrcidWork.class);
			Unmarshaller um = jc.createUnmarshaller();
			OrcidWork work = (OrcidWork) um.unmarshal(rep.getStream());
			String orcid = this.getAttribute("orcid");
			String token = this.getQueryValue("token");
			((OrcidOAuthClient) getContext().getAttributes().get("OrcidOAuthClient")).appendWork(orcid, token, work);
			this.setStatus(Status.SUCCESS_NO_CONTENT);
		} catch (JAXBException e) {
			this.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		}

		// the following fails on GAE due to missing JAXB security feature :(
		// hence using manual jaxb above.
		/*
		 * JaxbRepresentation<OrcidWork> jaxbRep = new
		 * JaxbRepresentation<OrcidWork>(rep, OrcidWork.class); OricdOAuthClient
		 * oauth = new OricdOAuthClient(); String orcid =
		 * this.getAttribute("orcid"); String token =
		 * this.getQueryValue("token"); oauth.appendWork(orcid, token,
		 * jaxbRep.getObject()); this.setStatus(Status.SUCCESS_NO_CONTENT);
		 */
	}

}
