package uk.bl.odin.orcid;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import uk.bl.odin.orcid.client.OrcidOAuthClient;
import uk.bl.odin.orcid.client.constants.OrcidAuthScope;

/** Does round trip log in and update profile using supplied constants.
 * requires: https://sites.google.com/a/chromium.org/chromedriver/getting-started 
 * 
 * Requires configuration in testoauth.properties
 * 
 * @author tom
 *
 */
//
public class OrcidOAuthClientTest {
	
	
	private Properties properties = new Properties();
	
	@Before
	public void before() throws IOException{
		 properties.load(getClass().getResourceAsStream("testoauth.properties"));
		 System.out.print(properties);
	}

	@SuppressWarnings("restriction")
	@Test
	public void testLoginAndUpdate() throws InterruptedException, JAXBException {
		OrcidOAuthClient client = new OrcidOAuthClient(properties.getProperty("orcidClientID"), properties.getProperty("orcidClientSecret"), properties.getProperty("orcidReturnUri"), Boolean.valueOf(properties.getProperty("orcidSandbox")));
		String authzreq = client.getAuthzCodeRequest(properties.getProperty("orcidWorkIdentifier"), OrcidAuthScope.CREATE_WORKS);		
		
		System.setProperty("webdriver.chrome.driver", properties.getProperty("chromeDriverLocation"));
		WebDriver driver = new ChromeDriver();

		driver.get(authzreq);
		
		WebElement u = driver.findElement(By.id("userId"));
		u.sendKeys(properties.getProperty("orcidUsername"));
		WebElement p = driver.findElement(By.id("password"));
		p.sendKeys(properties.getProperty("orcidPassword"));
		p.submit();
		
		WebElement element = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id("confirmationForm")));
		element.submit();
		
		WebElement updateElement = (new WebDriverWait(driver, 20)).until(ExpectedConditions.elementToBeClickable(By.id("updateButton")));
		assertEquals(properties.getProperty("orcidNumber"),driver.findElement(By.id("orcid")).getText());	
		updateElement.click();
		
		WebElement doneElement = (new WebDriverWait(driver, 20)).until(ExpectedConditions.elementToBeClickable(By.id("doneButton")));
		doneElement.click();
	}

}