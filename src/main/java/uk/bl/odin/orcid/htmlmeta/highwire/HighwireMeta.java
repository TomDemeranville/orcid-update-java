package uk.bl.odin.orcid.htmlmeta.highwire;

import java.util.Collection;
import java.util.Set;

import uk.bl.odin.orcid.client.constants.OrcidContributorRole;
import uk.bl.odin.orcid.client.constants.OrcidContributorSequence;
import uk.bl.odin.orcid.client.constants.OrcidExternalIdentifierType;
import uk.bl.odin.orcid.domain.IsOrcidWork;
import uk.bl.odin.orcid.htmlmeta.AbstractMeta;
import uk.bl.odin.orcid.htmlmeta.dc.DC_KEYS;
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

import com.google.appengine.repackaged.com.google.common.base.Splitter;
import com.google.common.collect.LinkedHashMultimap;

/*
 *see http://blog.reallywow.com/archives/123
 *see http://www.monperrus.net/martin/accurate+bibliographic+metadata+and+google+scholar
 */
public class HighwireMeta extends AbstractMeta<HW_KEYS> implements IsOrcidWork {

	@Override
	public OrcidWork toOrcidWork() {
		OrcidWork work = new OrcidWork();

		if (get(HW_KEYS.TITLE)!=null){
			WorkTitle title = new WorkTitle();
			title.setTitle(getFirst(HW_KEYS.TITLE));
			work.setWorkTitle(title);
		}
		
		/*
		if (get(DC_KEYS.DESCRIPTION)!=null){
			work.setShortDescription(getFirst(DC_KEYS.DESCRIPTION));
		}*/
		/*
		if (get(HW_KEYS.TYPE)!=null){
			//TODO match vocab with orcid
			work.setWorkType(getFirst(DC_KEYS.TYPE));
		}*/
		
		if (get(HW_KEYS.PUBLICATION_DATE)!=null){
			//TODO: check it's just a year
			PublicationDate publicationDate = new PublicationDate();
			Year year = new Year();
			year.setValue(getFirst(HW_KEYS.PUBLICATION_DATE));
			publicationDate.setYear(year);
			work.setPublicationDate(publicationDate);
		}
		
		if (get(HW_KEYS.DOI)!=null){
			WorkExternalIdentifiers wei = new WorkExternalIdentifiers();
			for (String s : get(HW_KEYS.DOI)) {
				WorkExternalIdentifier id = new WorkExternalIdentifier();
				id.setWorkExternalIdentifierType(OrcidExternalIdentifierType.DOI.toString());
			}
			work.setWorkExternalIdentifiers(wei);
		}		
		
		//TODO: refactor to get URL
		/*
		Url url = new Url();
		url.setValue(this.getUrl());
		work.setUrl(url);
		*/
		
		if (get(HW_KEYS.AUTHOR)!=null || get(HW_KEYS.AUTHORS)!=null){
			
			Iterable<String> a;
			if (get(HW_KEYS.AUTHOR)!=null)
				a= get(HW_KEYS.AUTHOR);
			else
				a = Splitter.on(';').split(getFirst(HW_KEYS.AUTHORS));
			
			WorkContributors contributors = new WorkContributors();
			boolean first = true;
			for (String nameString : a){
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
		
		if (getFirst(HW_KEYS.JOURNAL_TITLE) != null){
			JournalTitle title = new JournalTitle();
			title.setContent(getFirst(HW_KEYS.JOURNAL_TITLE));
			work.setJournalTitle(title);
		}
		
		work.setVisibility(Visibility.PUBLIC);
		return work;
		
	}

}
