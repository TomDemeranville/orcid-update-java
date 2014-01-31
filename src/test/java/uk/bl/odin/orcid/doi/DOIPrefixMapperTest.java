package uk.bl.odin.orcid.doi;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DOIPrefixMapperTest {
	
	@Test
	public final void test() {
		DOIPrefixMapper mapper = new DOIPrefixMapper();
		assertEquals(mapper.getPublisherMap().size(),3417);	
		assertEquals(mapper.getPublisherMap().get("10.1068"),"Pion Ltd.");
		assertEquals(mapper.getPublisherMapInverse().get("Cold Spring Harbor Laboratory Press").size(),3);
	}

}
