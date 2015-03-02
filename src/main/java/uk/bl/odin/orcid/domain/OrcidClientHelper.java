package uk.bl.odin.orcid.domain;

import java.util.HashSet;
import java.util.Set;

import uk.bl.odin.orcid.client.constants.OrcidWorkType;
import uk.bl.odin.orcid.schema.messages.onepointtwo.OrcidWork;
import uk.bl.odin.orcid.schema.messages.onepointtwo.OrcidWorks;
import uk.bl.odin.orcid.schema.messages.onepointtwo.WorkExternalIdentifier;

public class OrcidClientHelper {

	public Set<String> getIDs(OrcidWorks works, OrcidWorkType type) {
		Set<String> ret = new HashSet<String>();
		for (OrcidWork work : works.getOrcidWork()) {
			for (WorkExternalIdentifier id : work.getWorkExternalIdentifiers().getWorkExternalIdentifier()) {
				if (id.getWorkExternalIdentifierType().equals(type.toString()))
					ret.add(id.getWorkExternalIdentifierId());
			}
		}
		return ret;
	}

}
