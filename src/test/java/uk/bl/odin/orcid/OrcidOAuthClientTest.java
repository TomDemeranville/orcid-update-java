package uk.bl.odin.orcid;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import uk.bl.odin.orcid.client.OrcidAccessToken;
import uk.bl.odin.orcid.client.OrcidOAuthClient;
import uk.bl.odin.orcid.client.constants.OrcidAuthScope;

import static org.junit.Assert.*;

/**
 * Does round trip log in and update profile using supplied constants. requires:
 * https://sites.google.com/a/chromium.org/chromedriver/getting-started
 * 
 * Requires configuration in testoauth.properties and service to be available at
 * the orcidReturnUri
 * 
 * @author tom
 * 
 */
public class OrcidOAuthClientTest {

	private Properties properties = new Properties();

	@Before
	public void before() throws IOException {
		final String filename = "testoauth.properties";
		final InputStream inputStream = getClass().getResourceAsStream(filename);

		if (inputStream == null) {
			throw new IOException(
					"Unable to find properties file src/test/resources/uk/bl/odin/orcid/testoauth.properties"
							+ filename);
		}

		properties.load(inputStream);
	}

	/**
	 * This test relies on your hosts file/whatever redirecting the return URI
	 * to where your app resides. Your app must be running. e.g something like
	 * this in your hosts: 127.0.0.1 ethos-orcid.appspot.com and a simple proxy
	 * pointing port 80->8080
	 * 
	 * @throws InterruptedException
	 * @throws JAXBException
	 */
	@SuppressWarnings("restriction")
	// @Test
	public void testLoginAndUpdate() throws JAXBException, URISyntaxException, IOException {
		final String clientId = properties.getProperty("orcidClientID");
		final String clientSecret = properties.getProperty("orcidClientSecret");
		final String idNumber = properties.getProperty("orcidNumber");
		final String returnUri = properties.getProperty("orcidReturnUri");
		final String workIdentifier = properties.getProperty("orcidWorkIdentifier");
		final Boolean sandbox = Boolean.valueOf(properties.getProperty("orcidSandbox"));
		final OrcidAuthScope scope = OrcidAuthScope.CREATE_WORKS;

		OrcidOAuthClient client = new OrcidOAuthClient(clientId, clientSecret, returnUri, sandbox);
		String authzreq = client.getAuthzCodeRequest(workIdentifier, scope);

		System.setProperty("webdriver.chrome.driver", properties.getProperty("chromeDriverLocation"));
		WebDriver driver = new ChromeDriver();

		driver.get(authzreq);

		driver.findElement(By.id("in-register-switch-form")).click();

		WebElement u = driver.findElement(By.id("userId"));
		u.sendKeys(properties.getProperty("orcidUsername"));
		WebElement p = driver.findElement(By.id("password"));
		p.sendKeys(properties.getProperty("orcidPassword"));
		driver.findElement(By.id("authorize-button")).click();

		(new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getCurrentUrl().startsWith(returnUri);
			}
		});

		List<NameValuePair> pairs =
				URLEncodedUtils.parse(new URI(driver.getCurrentUrl()), Charset.defaultCharset().name());

		driver.close();

		String authorizationCode = null;
		for (NameValuePair pair : pairs) {
			if (pair.getName().equals("code")) {
				authorizationCode = pair.getValue();
				break;
			}
		}

		assertNotNull("No authorization code returned!", authorizationCode);

		OrcidAccessToken accessToken = client.getAccessToken(authorizationCode);

		assertNotNull(accessToken.getAccess_token());
		assertEquals(accessToken.getOrcid(), idNumber);
		assertEquals(accessToken.getScope(), scope.toString());
		assertTrue(accessToken.getExpires_in() > 0);
	}

}