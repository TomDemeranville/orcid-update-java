package uk.bl.odin.orcid.doi;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class DOIPrefixMapperTest {
	
	@Test
	public final void test() {
		DOIPrefixMapper mapper = new DOIPrefixMapper();
		assertEquals(mapper.getPublisherMap().size(),3417);	
		assertTrue(mapper.getPublisherMap().get("Pion Ltd.").contains("10.1068"));
		assertEquals(mapper.getPublisherMap().get("Pion Ltd.").size(),1);
		assertEquals(mapper.getPublisherMap().get("Cold Spring Harbor Laboratory Press").size(),3);
		
		assertTrue(mapper.getDatacentreMap().get("CDL.DPLANET - Data-Planet").contains("10.6068"));
		assertEquals(mapper.getDatacentreMap().get("CDL.DPLANET - Data-Planet").size(),1);
		assertTrue(mapper.getDatacentreMap().get("TIB.TIB - TIB Hannover").contains("10.2314"));
		assertTrue(mapper.getDatacentreMap().get("TIB.TIB - TIB Hannover").contains("10.2311"));
		assertEquals(mapper.getDatacentreMap().get("TIB.TIB - TIB Hannover").size(),2);
		
		
	}

}
