package uk.bl.odin.orcid.domain;

import java.io.IOException;

/** To use this application for you own purposes, implement this interface and
 * update MetadataFetchResource to use it.
 * 
 * @author tom
 */
public interface IsOrcidWorkProvider {
	public IsOrcidWork fetch(String id) throws IOException;
}
