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

	@Inject
	IsOrcidWorkProvider orcidWorkProvider;

	/**
	 * Fetches a metadata record and returns it as an XML OrcidWork. 
	 */
	@Get
	public Representation getMetadataAsOrcidWork() throws IOException {
		try {
			IsOrcidWork meta = orcidWorkProvider.fetch(this.getAttribute("id"));
			// problems with JaxbRepresentaion on java 1.7 GAE?  fixed in GAE 1.8.1 +
			return new JaxbRepresentation<OrcidWork>(meta.toOrcidWork());
		} catch (IOException e) {
			// TODO: make this fine grained - non existent, bad request and
			// server error.
			this.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "problem fetching metadata " + e.getMessage());
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
	public IsOrcidWork getEthosMetadata() throws IOException{
		try {
			return orcidWorkProvider.fetch(this.getAttribute("id"));
		} catch (IOException e) {
			// TODO: make this fine grained - non existent, bad request and
			// server error.
			this.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "problem fetching metadata " + e.getMessage());
			throw e;
		}
	}

}
