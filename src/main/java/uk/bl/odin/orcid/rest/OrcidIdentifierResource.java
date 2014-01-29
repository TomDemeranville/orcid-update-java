package uk.bl.odin.orcid.rest;

import java.util.ArrayList;
import java.util.List;

import org.restlet.data.Status;
import org.restlet.resource.Get;

import uk.bl.odin.orcid.client.constants.OrcidExternalIdentifierType;
import uk.bl.odin.orcid.client.constants.OrcidSearchField;
import uk.bl.odin.orcid.client.constants.OrcidWorkType;
import uk.bl.odin.orcid.domain.SearchType;
import uk.bl.odin.orcid.guice.SelfInjectingServerResource;

/** Returns an enumerated list for an identifier type.
 * 
 * Supported types are
 * "external" OrcidExternalIdentifierType
 * "worktype" OrcidWorkType
 * "searchfield" OrcidSearchField
 * "searchtype" SearchType
 * 
 * @author tom
 *
 */
public class OrcidIdentifierResource extends SelfInjectingServerResource{

	@Get("json")
	public List<String> getIdentifierList(){
		List<String> ids = new ArrayList<String>();
		if (getAttribute("type").equals("external")){
			for (OrcidExternalIdentifierType type: OrcidExternalIdentifierType.values()){
				ids.add(type.toString());
			}
		}else if (getAttribute("type").equals("worktype")){
			for (OrcidWorkType type: OrcidWorkType.values()){
				ids.add(type.toString());
			}
		}else if (getAttribute("type").equals("searchfield")){
			for (OrcidSearchField type: OrcidSearchField.values()){
				ids.add(type.toString());
			}
		}else if (getAttribute("type").equals("searchtype")){
			for (SearchType type: SearchType.values()){
				ids.add(type.toString());
			}
		}
		if (ids.size()>0)
			return ids;
		setStatus(Status.CLIENT_ERROR_BAD_REQUEST,"invalid identifier type");
		return null;
	}
	
}
