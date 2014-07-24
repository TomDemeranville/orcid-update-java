package uk.bl.odin.orcid.htmlmeta.prism;

//TODO: more voacb
public enum PM_KEYS {
	TITLE("prism.title"), 
	VALUE("prism.volume"), 
	NUMBER("prism.number"), 
	STARTING_PAGE("prism.startingPage" ),
	ENDING_PAGE("prism.endingPage" ),
	PUBLICATION_NAME("prism.publicationName" ),
	ISSN("prism.issn" ),
	PUBLICATION_DATE("prism.publicationDate" ),
	DOI("prism.doi" );
	
	private final String stringValue;

	private PM_KEYS(final String s) {
		stringValue = s;
	}

	public String toString() {
		return stringValue;
	}
}