package uk.bl.odin.orcid.rest.report;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import uk.bl.odin.orcid.doi.DOIPrefixMapper;
import uk.bl.odin.orcid.guice.SelfInjectingServerResource;

import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.ImmutableMultimap;

public class DOIPrefixResource extends SelfInjectingServerResource{

	@Inject
	DOIPrefixMapper mapper;
	
	@Get("json")
	public Representation getPrefixMap(){
		if (this.getQueryValue("style")!=null && this.getQueryValue("style").equals("inverse")){			
			JacksonRepresentation<ImmutableMultimap<String, String>> r = 
					new JacksonRepresentation<ImmutableMultimap<String, String>>(mapper.getPublisherMapInverse());
			r.getObjectMapper().registerModule(new GuavaModule());
			return r;
		}else{
			List<TypeaheadJSBean> list = new ArrayList<TypeaheadJSBean>();
			for (String s: mapper.getPublisherMap().keySet()){
				list.add(new TypeaheadJSBean(s,mapper.getPublisherMap().get(s)));
			}
			return new JacksonRepresentation<List<TypeaheadJSBean>>(list);
		}
	}
	
	public static class TypeaheadJSBean{
		public TypeaheadJSBean(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}
		public String name;
		public String value;
	}

}
