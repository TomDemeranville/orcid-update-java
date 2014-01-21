package uk.bl.odin.orcid.rest;

import java.io.IOException;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.restlet.data.Status;
import org.restlet.ext.jaxb.JaxbRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import uk.bl.odin.orcid.domain.IsOrcidWork;
import uk.bl.odin.orcid.domain.IsOrcidWorkProvider;
import uk.bl.odin.orcid.guice.SelfInjectingServerResource;
import uk.bl.odin.schema.orcid.messages.onepointone.OrcidWork;

public class MetadataFetchResource extends SelfInjectingServerResource {

	@Inject IsOrcidWorkProvider orcidWorkProvider;
	
	/**
	 * Fetches a metadata record and returns it as an XML OrcidWork. Requires
	 * java 1.6 not 1.7 due to JAXB limitations on GAE.
	 */
	@Get
	public Representation getMetadataAsOrcidWork() throws JAXBException, IOException {
		try {
			String id = this.getAttribute("id");
			IsOrcidWork meta = orcidWorkProvider.fetch(id);
			// TODO: do this manually. There are all sorts of JAXP security
			// problems with JaxbRepresentaion on java 1.7
			return new JaxbRepresentation<OrcidWork>(meta.toOrcidWork());
		} catch (IOException e) {
			// TODO: make this fine grained - non existent, bad request and
			// server error.
			this.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "problem fetching metadata " + e);
			throw e;
		}
	}

	/**
	 * Fetches a JSON representation of a metadata document Note this returns
	 * the RAW form of the object as JSON, not the ORCID Transformed form So
	 * depends on the underlying implementation of the class returned by the
	 * provider. This is useful for user confirmation etc.
	 */
	@Get("?json")
	public IsOrcidWork getEthosMetadata() {
		String id = this.getAttribute("id");
		try {
			return orcidWorkProvider.fetch(id);
		} catch (IOException e) {
			this.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "problem fetching metadata");
			return null;
		}
	}

}
