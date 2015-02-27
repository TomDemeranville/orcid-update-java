package uk.bl.odin.orcid.htmlmeta.prism;

import uk.bl.odin.orcid.domain.IsOrcidWork;
import uk.bl.odin.orcid.schema.messages.onepointtwo.OrcidWork;

public class PrismMeta implements IsOrcidWork{

	@Override
	public OrcidWork toOrcidWork() {
		throw new UnsupportedOperationException();
	}

}
