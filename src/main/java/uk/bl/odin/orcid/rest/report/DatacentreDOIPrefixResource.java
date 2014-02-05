package uk.bl.odin.orcid.rest.report;

import java.util.List;

import javax.inject.Inject;

import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import uk.bl.odin.orcid.doi.DOIPrefixMapper;
import uk.bl.odin.orcid.guice.SelfInjectingServerResource;
import uk.bl.odin.orcid.rest.report.PublisherDOIPrefixResource.TypeaheadJSBean;

public class DatacentreDOIPrefixResource extends SelfInjectingServerResource {

	@Inject
	DOIPrefixMapper mapper;

	@Get("json")
	public Representation getPrefixMap() {
		return new JacksonRepresentation<List<TypeaheadJSBean>>(TypeaheadJSBean.fromMultimap(mapper.getDatacentreMap()));
	}

}
