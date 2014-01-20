package uk.bl.odin.orcid.ethos;

import java.util.ArrayList;
import java.util.List;

import uk.bl.odin.orcid.domain.BibtexBuilder;
import uk.bl.odin.orcid.domain.IsOrcidWork;
import uk.bl.odin.orcid.domain.OrcidConstants;
import uk.bl.odin.schema.orcid.messages.onepointone.Citation;
import uk.bl.odin.schema.orcid.messages.onepointone.CitationType;
import uk.bl.odin.schema.orcid.messages.onepointone.Contributor;
import uk.bl.odin.schema.orcid.messages.onepointone.ContributorAttributes;
import uk.bl.odin.schema.orcid.messages.onepointone.CreditName;
import uk.bl.odin.schema.orcid.messages.onepointone.OrcidWork;
import uk.bl.odin.schema.orcid.messages.onepointone.PublicationDate;
import uk.bl.odin.schema.orcid.messages.onepointone.Url;
import uk.bl.odin.schema.orcid.messages.onepointone.Visibility;
import uk.bl.odin.schema.orcid.messages.onepointone.WorkContributors;
import uk.bl.odin.schema.orcid.messages.onepointone.WorkExternalIdentifier;
import uk.bl.odin.schema.orcid.messages.onepointone.WorkExternalIdentifiers;
import uk.bl.odin.schema.orcid.messages.onepointone.WorkTitle;
import uk.bl.odin.schema.orcid.messages.onepointone.Year;

/** Models the DC metadata we can extract from ETHOS HTML meta tags and transforms them into OrcidWorks
 */
public class ThesisMetadata implements IsOrcidWork {

	private String creator;
	private String publisher;
	private String title;
	private String abstract_;
	private String year;
	private String url;
	// one for ethos and (optionally) one for institution
	private List<String> identifiers = new ArrayList<String>();
	private String thesisType;

	// private String dcLanguage;

	/** Transform into an OrcidWork
	 * 
	 * @return a JAXB annotated object hierarchy
	 */
	public OrcidWork toOrcidWork() {

		OrcidWork work = new OrcidWork();

		WorkTitle title = new WorkTitle();
		title.setTitle(this.getTitle());
		work.setWorkTitle(title);

		work.setShortDescription(this.getAbstract());

		Citation citation = new Citation();
		citation.setCitation(this.getBibtex());
		citation.setWorkCitationType(CitationType.BIBTEX);
		work.setWorkCitation(citation);

		work.setWorkType(OrcidConstants.WORK_TYPE_DISSERTATION);

		PublicationDate publicationDate = new PublicationDate();
		Year year = new Year();
		year.setValue(this.getYear());
		publicationDate.setYear(year);
		work.setPublicationDate(publicationDate);

		WorkExternalIdentifiers wei = new WorkExternalIdentifiers();

		for (String s : this.getIdentifiers()) {
			WorkExternalIdentifier weEthos = new WorkExternalIdentifier();
			weEthos.setWorkExternalIdentifierId(s);
			weEthos.setWorkExternalIdentifierType(OrcidConstants.IDENTIFIER_TYPE_OTHER);
			wei.getWorkExternalIdentifier().add(weEthos);
		}

		Url url = new Url();
		url.setValue(this.getUrl());
		work.setUrl(url);

		// note language is en for orcid, eng for ethos!
		// TODO: work out if we need this.

		WorkContributors contributors = new WorkContributors();
		Contributor contributor = new Contributor();
		CreditName name = new CreditName();
		name.setValue(this.getCreator());
		contributor.setCreditName(name);
		ContributorAttributes attributes = new ContributorAttributes();
		attributes.setContributorRole(OrcidConstants.ROLE_AUTHOR);
		attributes.setContributorSequence(OrcidConstants.SEQUENCE_FIRST);
		contributor.setContributorAttributes(attributes);
		contributors.getContributor().add(contributor);
		work.setWorkContributors(contributors);

		work.setVisibility(Visibility.PUBLIC);

		return work;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAbstract() {
		return abstract_;
	}

	public void setAbstract(String abstract_) {
		this.abstract_ = abstract_;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public List<String> getIdentifiers() {
		return identifiers;
	}

	public void setIdentifiers(List<String> identifiers) {
		this.identifiers = identifiers;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Converts this metadata into a bibtext citation on request
	 */
	public String getBibtex() {
		return BibtexBuilder.getInstance().buildPHDCitation(getCreator(), getTitle(), getPublisher(), getYear());
	}

	public String getThesisType() {
		return thesisType;
	}

	public void setThesisType(String thesisType) {
		this.thesisType = thesisType;
	}

}
