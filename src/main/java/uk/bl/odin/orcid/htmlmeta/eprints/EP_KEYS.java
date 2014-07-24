package uk.bl.odin.orcid.htmlmeta.eprints;

public enum EP_KEYS {
	TITLE("eprints.title"), 
	CREATORS_NAME("eprints.creators_name"),
	DATE("eprints.date"),
	PUBLISHER("eprints.publisher"),
	PUBLICATION("eprints.publication"),
	VOLUME("eprints.volume"),
	NUMBER("eprints.volume"),/*issue*/
	PAGERANGE("eprints.pagerange"),
	TYPE("eprints.type"),
	OFFICIAL_URL("eprints.official_url")/*sometimes a doi*/,
	ID_NUMBER("eprints.id_number"),/*sometimes a doi*/
	ISBN("eprints.isbn"),
	BOOK_TITLE("eprints.book_title"),
	CITATION("eprints.citation");
	
	/*
	 * <meta content="doi: 10.1002/nag.2230" name="eprints.id_number" />
	 */
	
	/*
	<meta name="eprints.event_title" content="2013 48th International Universities' Power Engineering Conference, UPEC 2013" />
	<meta name="eprints.event_location" content="Dublin" />
	<meta name="eprints.event_dates" content="2013-09-02 - 2013-09-05" />
	<meta name="eprints.isbn" content="9781479932542" />
	<meta name="eprints.book_title" content="Proceedings of the Universities Power Engineering Conference" />
	*/
	
	/*
	 * TWO SETS of DC METADATA in single doc.  One for uni web staff.
	OMG - opus.bath.ac.uk/38755/
	*/
	
	private final String stringValue;

	private EP_KEYS(final String s) {
		stringValue = s;
	}

	public String toString() {
		return stringValue;
	}
}
