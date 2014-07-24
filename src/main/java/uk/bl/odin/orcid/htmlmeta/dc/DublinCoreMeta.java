package uk.bl.odin.orcid.htmlmeta.dc;

import java.util.Set;

import com.google.common.collect.LinkedHashMultimap;

import uk.bl.odin.orcid.client.constants.OrcidContributorRole;
import uk.bl.odin.orcid.client.constants.OrcidContributorSequence;
import uk.bl.odin.orcid.client.constants.OrcidExternalIdentifierType;
import uk.bl.odin.orcid.domain.BibtexBuilder;
import uk.bl.odin.orcid.domain.IsOrcidWork;
import uk.bl.odin.orcid.htmlmeta.AbstractMeta;
import uk.bl.odin.orcid.schema.messages.onepointone.Citation;
import uk.bl.odin.orcid.schema.messages.onepointone.CitationType;
import uk.bl.odin.orcid.schema.messages.onepointone.Contributor;
import uk.bl.odin.orcid.schema.messages.onepointone.ContributorAttributes;
import uk.bl.odin.orcid.schema.messages.onepointone.CreditName;
import uk.bl.odin.orcid.schema.messages.onepointone.OrcidWork;
import uk.bl.odin.orcid.schema.messages.onepointone.PublicationDate;
import uk.bl.odin.orcid.schema.messages.onepointone.Url;
import uk.bl.odin.orcid.schema.messages.onepointone.Visibility;
import uk.bl.odin.orcid.schema.messages.onepointone.WorkContributors;
import uk.bl.odin.orcid.schema.messages.onepointone.WorkExternalIdentifier;
import uk.bl.odin.orcid.schema.messages.onepointone.WorkExternalIdentifiers;
import uk.bl.odin.orcid.schema.messages.onepointone.WorkTitle;
import uk.bl.odin.orcid.schema.messages.onepointone.Year;

/**
 * Maps DC metadata (maybe plus issued from DCTERMS - as per google scholar - investigate) to OrcidWorks // http://dublincore.org/documents/dcq-html/
 * // http://dublincore.org/documents/dcmi-terms/ //
 * http://dublincore.org/documents/dces/
 * 
 * @author tom
 */
public class DublinCoreMeta extends AbstractMeta<DC_KEYS> implements IsOrcidWork {

	public static String DC_PREFIX = "DC";
	public static String DCTERMS_PREFIX = "DCTERMS";
	
	@Override
	public OrcidWork toOrcidWork() {
		
		
		OrcidWork work = new OrcidWork();

		if (get(DC_KEYS.TITLE)!=null){
			WorkTitle title = new WorkTitle();
			title.setTitle(getFirst(DC_KEYS.TITLE));
			work.setWorkTitle(title);
		}
		
		if (get(DC_KEYS.DESCRIPTION)!=null){
			work.setShortDescription(getFirst(DC_KEYS.DESCRIPTION));
		}

		if (get(DC_KEYS.TYPE)!=null){
			//TODO match vocab with orcid
			//TODO match multiokes
			work.setWorkType(getFirst(DC_KEYS.TYPE));
		}
		
		if (get(DC_KEYS.DATE)!=null){
			//TODO: check it's just a year
			PublicationDate publicationDate = new PublicationDate();
			Year year = new Year();
			year.setValue(getFirst(DC_KEYS.DATE));
			publicationDate.setYear(year);
			work.setPublicationDate(publicationDate);
		}
		
		if (get(DC_KEYS.IDENTIFIER)!=null){
			WorkExternalIdentifiers wei = new WorkExternalIdentifiers();
			for (String s : get(DC_KEYS.IDENTIFIER)) {
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
		
		//TODO: refactor to get URL
		/*
		Url url = new Url();
		url.setValue(this.getUrl());
		work.setUrl(url);
		*/
		
		//TODO - get contributors out, it's the examiner or supervisor - no equiv ORCiD role though.
		if (get(DC_KEYS.CREATOR)!=null){
			WorkContributors contributors = new WorkContributors();
			boolean first = true;
			for (String nameString : get(DC_KEYS.CREATOR)){
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
		
		if (get(DC_KEYS.PUBLISHER)!=null){
			//ORCiD has no field for publisher!!!
		}
		
		try{
			if (work.getWorkType().equals("thesis")){
				Citation citation = new Citation(); 
				citation.setCitation(BibtexBuilder.getInstance().buildPHDCitation(getFirst(DC_KEYS.CREATOR), getFirst(DC_KEYS.TITLE), getFirst(DC_KEYS.PUBLISHER), getFirst(DC_KEYS.DATE)));
				citation.setWorkCitationType(CitationType.BIBTEX);
				work.setWorkCitation(citation);
			}
		}catch(Exception e){
			//TODO proper exception handling for missing fields
		}
		
		work.setVisibility(Visibility.PUBLIC);
		return work;
	}

	/*
	 * public static String DCTERMS_PREFIX = "DCTERMS"; public static
	 * List<String> DCTERMSKEYS = Arrays.asList(new String[] { "abstract",
	 * "accessRights", "accrualMethod", "accrualPeriodicity", "accrualPolicy",
	 * "alternative", "audience", "available", "bibliographicCitation",
	 * "conformsTo", "contributor", "coverage", "created", "creator", "date",
	 * "dateAccepted", "dateCopyrighted", "dateSubmitted", "description",
	 * "educationLevel", "extent", "format", "hasFormat", "hasPart",
	 * "hasVersion", "identifier", "instructionalMethod", "isFormatOf",
	 * "isPartOf", "isReferencedBy", "isReplacedBy", "isRequiredBy", "issued",
	 * "isVersionOf", "language", "license", "mediator", "medium", "modified",
	 * "provenance", "publisher", "references", "relation", "replaces",
	 * "requires", "rights", "rightsHolder", "source", "spatial", "subject",
	 * "tableOfContents", "temporal", "title", "type", "valid" });
	 * 
	 * public static List<String> DCKEYS = Arrays.asList(new String[] {
	 * "contributor", "coverage", "creator", "date", "description", "format",
	 * "identifier", "language", "publisher", "relation", "rights", "source",
	 * "subject", "title", "type" });
	 */
}
