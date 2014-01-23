import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.restlet.representation.Representation;
import org.restlet.resource.Resource;

import uk.bl.odin.orcid.domain.OrcidPublicClient;
import uk.bl.odin.schema.orcid.messages.onepointone.OrcidProfile;
import uk.bl.odin.schema.orcid.messages.onepointone.OrcidSearchResults;


public class OrcidPublicClientTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testFetchProfile() throws IOException {
		OrcidPublicClient client = new OrcidPublicClient();
	
		OrcidProfile pro = client.getProfile("0000-0002-9151-6445", OrcidPublicClient.TYPE_ORCID_PROFILE);
		assertEquals("Unit",pro.getOrcidBio().getPersonalDetails().getGivenNames());
		assertEquals("Test",pro.getOrcidBio().getPersonalDetails().getFamilyName());
		assertNotNull(pro.getOrcidActivities().getOrcidWorks().getOrcidWork());
		assertEquals(pro.getOrcidIdentifier().getPath(),"0000-0002-9151-6445");
		
		//No works.
		OrcidProfile bio = client.getProfile("0000-0002-9151-6445", OrcidPublicClient.TYPE_ORCID_BIO);
		assertEquals("Unit",bio.getOrcidBio().getPersonalDetails().getGivenNames());
		assertEquals("Test",bio.getOrcidBio().getPersonalDetails().getFamilyName());
		assertNull(bio.getOrcidActivities().getOrcidWorks());
		assertEquals(bio.getOrcidIdentifier().getPath(),"0000-0002-9151-6445");
	}
	
	//TODO: chose a better test target (that won't change)
	@Test
	public final void testSearchForDOI() throws IOException{
		
		//THIS IS RETURNING INVALID MESSAGES - they're 1.0.23 (have an <orcid> element) despite being labeled as 1.1
		OrcidPublicClient client = new OrcidPublicClient();
		String query = OrcidPublicClient.buildDOIQuery("10.6084/m9.figshare.909352");
		assertEquals("digital-object-ids: \"10.6084/m9.figshare.909352\"",query);
		OrcidSearchResults results = client.search(query);
		assertEquals(1,results.getNumFound().intValue());
		assertEquals(results.getOrcidSearchResult().get(0).getOrcidProfile().getOrcidIdentifier().getPath(),"0000-0002-9151-6445");
	}

	//invalid 1.1 response!! They're 1.0.X - orcid-profiles have <orcid> elements
	/*
	 <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<orcid-message xmlns="http://www.orcid.org/ns/orcid">
    <message-version>1.1</message-version>
    <orcid-search-results num-found="1">
        <orcid-search-result>
            <relevancy-score>15.793974</relevancy-score>
            <orcid-profile>
                <orcid>0000-0003-0902-4386</orcid>
                <orcid-identifier>
                    <uri>http://orcid.org/0000-0003-0902-4386</uri>
                    <path>0000-0003-0902-4386</path>
                    <host>orcid.org</host>
                </orcid-identifier>
                <orcid-bio>
                    <personal-details>
                        <given-names>Tom</given-names>
                        <family-name>Demeranville</family-name>
                    </personal-details>
                </orcid-bio>
                <orcid-activities/>
            </orcid-profile>
        </orcid-search-result>
    </orcid-search-results>
</orcid-message>
	 */
}
