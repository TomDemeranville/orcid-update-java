package uk.bl.odin.orcid.htmlmeta.highwire;

public enum HW_KEYS {
	TITLE("citation_title"), AUTHORS("citation_authors"), AUTHOR("citation_author"), JOURNAL_TITLE("citation_journal_title"), PUBLISHER(
			"citation_publisher"), ISSUE("citation_issue"), VOLUME("citation_volume"), DOI("citation_doi"), FIRSTPAGE(
			"citation_firstpage"), LASTPAGE("citation_lastpage"), DATE("citation_date"), PUBLICATION_DATE("citation_publication_date");
	private final String stringValue;

	private HW_KEYS(final String s) {
		stringValue = s;
	}

	public String toString() {
		return stringValue;
	}
}