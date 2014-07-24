package uk.bl.odin.orcid.htmlmeta.eprints;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.appengine.repackaged.com.google.common.base.Splitter;

import uk.bl.odin.orcid.client.constants.OrcidContributorRole;
import uk.bl.odin.orcid.client.constants.OrcidContributorSequence;
import uk.bl.odin.orcid.client.constants.OrcidExternalIdentifierType;
import uk.bl.odin.orcid.client.constants.OrcidWorkType;
import uk.bl.odin.orcid.domain.IsOrcidWork;
import uk.bl.odin.orcid.htmlmeta.AbstractMeta;
import uk.bl.odin.orcid.htmlmeta.dc.DC_KEYS;
import uk.bl.odin.orcid.htmlmeta.highwire.HW_KEYS;
import uk.bl.odin.orcid.schema.messages.onepointone.Citation;
import uk.bl.odin.orcid.schema.messages.onepointone.CitationType;
import uk.bl.odin.orcid.schema.messages.onepointone.Contributor;
import uk.bl.odin.orcid.schema.messages.onepointone.ContributorAttributes;
import uk.bl.odin.orcid.schema.messages.onepointone.CreditName;
import uk.bl.odin.orcid.schema.messages.onepointone.JournalTitle;
import uk.bl.odin.orcid.schema.messages.onepointone.OrcidWork;
import uk.bl.odin.orcid.schema.messages.onepointone.PublicationDate;
import uk.bl.odin.orcid.schema.messages.onepointone.Visibility;
import uk.bl.odin.orcid.schema.messages.onepointone.WorkContributors;
import uk.bl.odin.orcid.schema.messages.onepointone.WorkExternalIdentifier;
import uk.bl.odin.orcid.schema.messages.onepointone.WorkExternalIdentifiers;
import uk.bl.odin.orcid.schema.messages.onepointone.WorkTitle;
import uk.bl.odin.orcid.schema.messages.onepointone.Year;

public class EPrintsMeta extends AbstractMeta<EP_KEYS> implements IsOrcidWork{
	
	private static Pattern yearmatcher = Pattern.compile("\\d{4}");
	
	/** Really, really ropey implementation
	 * 
	 */
	@Override
	public OrcidWork toOrcidWork() {
		OrcidWork work = new OrcidWork();
		if (has(EP_KEYS.TITLE)){
			WorkTitle title = new WorkTitle();
			title.setTitle(getFirst(EP_KEYS.TITLE));
			work.setWorkTitle(title);
		}
		
		if (has(EP_KEYS.TYPE)){
			if (getFirst(EP_KEYS.TYPE).equals("thesis"))
					work.setWorkType(OrcidWorkType.DISSERTAION.toString());
			else if (getFirst(EP_KEYS.TYPE).equals("article"))
				work.setWorkType(OrcidWorkType.JOURNAL_ARTICLE.toString());
			else if (getFirst(EP_KEYS.TYPE).equals("dataset"))
				work.setWorkType(OrcidWorkType.DATASET.toString());
			//example dataset http://era.deedi.qld.gov.au/3582/
			else
				work.setWorkType(OrcidWorkType.OTHER.toString());
		}
		
		if (has(EP_KEYS.CREATORS_NAME)){
			WorkContributors contributors = new WorkContributors();
			boolean first = true;
			for (String nameString : get(EP_KEYS.CREATORS_NAME)){
				Contributor contributor = new Contributor();
				CreditName name = new CreditName();
				name.setValue(nameString);
				contributor.setCreditName(name);
				ContributorAttributes attributes = new ContributorAttributes();
				attributes.setContributorRole(OrcidContributorRole.AUTHOR.toString());
				if (first){
					attributes.setContributorSequence(OrcidContributorSequence.FIRST.toString());
					first = false;
				}else{
					attributes.setContributorSequence(OrcidContributorSequence.ADDITIONAL.toString());
				}
				contributor.setContributorAttributes(attributes);
				contributors.getContributor().add(contributor);
			}
			work.setWorkContributors(contributors);
		}
		
		if (has(EP_KEYS.PUBLICATION)){
			JournalTitle title = new JournalTitle();
			title.setContent(getFirst(EP_KEYS.PUBLICATION));
			work.setJournalTitle(title);
		}
		
		if (has(EP_KEYS.DATE)){
			//TODO: check it's just a year
			System.out.println(getFirst(EP_KEYS.DATE));
			Matcher m = yearmatcher.matcher(getFirst(EP_KEYS.DATE));
			if (m.find()){
				PublicationDate publicationDate = new PublicationDate();
				Year year = new Year();
				year.setValue(m.group());
				publicationDate.setYear(year);
				work.setPublicationDate(publicationDate);
			}
		}
		
		if (has(EP_KEYS.CITATION)){
			Citation c = new Citation();
			c.setCitation(getFirst(EP_KEYS.CITATION));
			c.setWorkCitationType(CitationType.FORMATTED_UNSPECIFIED);//is this always true?
			work.setWorkCitation(c);
		}
		
		if (has(EP_KEYS.ID_NUMBER)){
			WorkExternalIdentifiers wei = new WorkExternalIdentifiers();
			for (String s : get(EP_KEYS.ID_NUMBER)) {
				WorkExternalIdentifier id = new WorkExternalIdentifier();
				id.setWorkExternalIdentifierId(s);
				// TODO: identify if we have a handle or doi or whatever.
				// TODO: parse out weird prefixes seen in the wild, like PMID: or DOI:
				if (s.contains("doi") || s.contains("DOI") || s.startsWith("10."))
					id.setWorkExternalIdentifierType(OrcidExternalIdentifierType.DOI.toString());
				else
					id.setWorkExternalIdentifierType(OrcidExternalIdentifierType.OTHER_ID.toString());
				wei.getWorkExternalIdentifier().add(id);
			}
			work.setWorkExternalIdentifiers(wei);
		}
		
		if (has(EP_KEYS.ID_NUMBER)){
			WorkExternalIdentifiers wei = new WorkExternalIdentifiers();
			for (String s : get(EP_KEYS.ID_NUMBER)) {
				WorkExternalIdentifier id = new WorkExternalIdentifier();
				id.setWorkExternalIdentifierId(s);
				// TODO: identify if we have a handle or doi or whatever.
				// TODO: parse out weird prefixes seen in the wild, like PMID: or DOI:
				if (s.contains("doi") || s.contains("DOI") || s.startsWith("10."))
					id.setWorkExternalIdentifierType(OrcidExternalIdentifierType.DOI.toString());
				else
					id.setWorkExternalIdentifierType(OrcidExternalIdentifierType.OTHER_ID.toString());
				wei.getWorkExternalIdentifier().add(id);
			}
			work.setWorkExternalIdentifiers(wei);
		}
		
		work.setVisibility(Visibility.PUBLIC);
		return work;
	}
}


