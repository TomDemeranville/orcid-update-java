package uk.bl.odin.orcid.htmlmeta.dc;

public enum DC_KEYS {
	CONTRIBUTOR("contributor"), COVERAGE("coverage"), CREATOR("creator"), DATE("date"), DESCRIPTION("description"), FORMAT(
			"format"), IDENTIFIER("identifier"), LANGUAGE("language"), PUBLISHER("publisher"), RELATION("relation"), RIGHTS(
			"rights"), SOURCE("source"), SUBJECT("subject"), TITLE("title"), TYPE("type");
	
	//sometimes relation is a DOI and identifier is a citation.  Doh.
	//need dcterms too! "issued"?
	private final String stringValue;

	private DC_KEYS(final String s) {
		stringValue = s;
	}

	public String toString() {
		return stringValue;
	}
}