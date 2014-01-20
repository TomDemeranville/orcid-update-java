package uk.bl.odin.orcid.domain;

import junit.framework.TestCase;

public class BibtextBuilderTest extends TestCase {

	private static String result1 = "@PhDThesis{authoryeart,\nauthor = {author\\%\\&\\$\\{},\ntitle = {title{'}s},\nschool = {institution},\nyear = year\n}";
	
	//VERY SIMPLE TEST CASE
	public void testBuildPHDCitation(){
		String bibtext1 = BibtexBuilder.getInstance().buildPHDCitation("author%&${", "title's", "institution", "year");
		assertEquals(bibtext1,result1);
	}

}
