package uk.bl.odin.orcid.rest;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import uk.bl.odin.orcid.client.OrcidAccessToken;
import uk.bl.odin.orcid.client.OrcidOAuthClient;
import uk.bl.odin.orcid.guice.SelfInjectingServerResource;
import uk.bl.odin.orcid.schema.messages.onepointtwo.OrcidWork;

public class OrcidWorkCreationResource extends SelfInjectingServerResource {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(OrcidWorkCreationResource.class.getName());

	@Inject
	OrcidOAuthClient orcidOAuthClient;

	private static JAXBContext orcidWorkJAXBContext;// =
													// JAXBContext.newInstance(OrcidWork.class);

	@Override
	public void doInit() {
		super.doInit();
		if (orcidWorkJAXBContext == null) {
			try {
				orcidWorkJAXBContext = JAXBContext.newInstance(OrcidWork.class);
			} catch (JAXBException e) {
				this.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			}
		}
	}

	/**
	 * Accepts an XML encoded ORCID work. POSTs work to ORCID as a new work.
	 * Requires ?token= query param containing ORCID auth token.
	 * 
	 * @param rep
	 *            a serialised OrcidWork
	 * @throws IOException
	 *             if we can't append. (TODO: handle properly)
	 */
	@Post
	public void addWork(Representation rep) {
		try {
			Unmarshaller um = orcidWorkJAXBContext.createUnmarshaller();
			OrcidWork work = (OrcidWork) um.unmarshal(rep.getStream());
			OrcidAccessToken token = new OrcidAccessToken();
			token.setOrcid(this.getAttribute("orcid"));
			token.setAccess_token(this.getQueryValue("token"));
			orcidOAuthClient.appendWork(token, work);
			this.setStatus(Status.SUCCESS_NO_CONTENT);
		} catch (JAXBException e) {
			this.setStatus(Status.SERVER_ERROR_INTERNAL, e);
		} catch (ResourceException e) {
			e.printStackTrace();
			this.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e);
		} catch (IOException e) {
			this.setStatus(Status.SERVER_ERROR_BAD_GATEWAY, e);
		}

		// TODO: test on GAE 1.8.1+
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
