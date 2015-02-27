package uk.bl.odin.orcid.domain;

import uk.bl.odin.orcid.schema.messages.onepointtwo.OrcidWork;

/**
 * Models anything that can be transformed into a JAXB model of an OrcidWork
 * 
 * @author tom
 */
public interface IsOrcidWork {
	public OrcidWork toOrcidWork();
}
