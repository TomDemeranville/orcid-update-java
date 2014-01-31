package uk.bl.odin.orcid;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Context;

import uk.bl.odin.orcid.client.OrcidOAuthClient;
import uk.bl.odin.orcid.client.OrcidPublicClient;
import uk.bl.odin.orcid.domain.CacheManager;
import uk.bl.odin.orcid.domain.IsOrcidWorkProvider;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * Manages dependencies. Uses context passed from Servlet Init params.
 * 
 * @author tom
 * 
 */
public class RootGuiceModule extends AbstractModule {

	private static final Logger log = Logger.getLogger(RootGuiceModule.class.getName());
	private Context context;
	public static final String CONFIG_KEY_OrcidClientID = "OrcidClientID";
	public static final String CONFIG_KEY_OrcidClientSecret = "OrcidClientSecret";
	public static final String CONFIG_KEY_OrcidReturnURI = "OrcidReturnURI";
	public static final String CONFIG_KEY_OrcidSandbox = "OrcidSandbox";
	public static final String CONFIG_KEY_OrcidWorkProvider = "OrcidWorkProvider";

	public static final String CONFIG_KEY_CacheTimeout = "OrcidCacheTimeout";
	public static final String CONFIG_KEY_CacheMaxsize = "OrcidCacheMaxsize";

	public RootGuiceModule(Context context) {
		super();
		this.context = context;
	}

	/**
	 * Validates configuration and creates dependencies. Uses config from
	 * web.xml
	 * 
	 * Init params
	 * <ul>
	 * <li>"OrcidWorkProvider" fully qualified class name for
	 * IsOrcidWorkProvider instance</li>
	 * <li>"OrcidClientID", "OrcidClientSecret", "OrcidReturnURI" ORCID OAuth
	 * params</li>
	 * <li>"OrcidSandbox" true for sandbox, otherwise use live api</li>
	 * </ul>
	 * 
	 * Binds a singleton OrcidOAuthClient & the configured IsOrcidWorkProvider
	 * class as not-a-singleton Binds passed config as
	 * Named("{init_param_name}")
	 * 
	 */
	@Override
	protected void configure() {
		// validate OAuth configuration
		if (context.getParameters().getFirst(CONFIG_KEY_OrcidClientID) == null
				|| context.getParameters().getFirst(CONFIG_KEY_OrcidClientSecret) == null
				|| context.getParameters().getFirst(CONFIG_KEY_OrcidReturnURI) == null
				|| context.getParameters().getFirst(CONFIG_KEY_OrcidSandbox) == null) {
			log.severe("Init params are:  " + context.getParameters().toString());
			throw new IllegalStateException("cannot create OrcidOAuthClient - missing init parameter(s)");
		}
		if (context.getParameters().getFirst(CONFIG_KEY_OrcidWorkProvider) == null) {
			log.severe("Init params are:  " + context.getParameters().toString());
			throw new IllegalStateException("cannot create OrcidWorkProvier - missing init parameter");
		}
		
		//Resolve OrcidWork provider 
		IsOrcidWorkProvider provider;
		try {
			provider = (IsOrcidWorkProvider) Class.forName(
					context.getParameters().getFirst(CONFIG_KEY_OrcidWorkProvider).getValue()).newInstance();
		} catch (Exception e) {
			throw new IllegalStateException("cannot create OrcidWorkProvier", e);
		}

		// Suppress Guice warning when on GAE
		// see https://code.google.com/p/google-guice/issues/detail?id=488
		Logger.getLogger("com.google.inject.internal.util").setLevel(Level.WARNING);

		// bind config params
		bind(String.class).annotatedWith(Names.named(CONFIG_KEY_OrcidClientID)).toInstance(
				context.getParameters().getFirst(CONFIG_KEY_OrcidClientID).getValue().toString());
		bind(String.class).annotatedWith(Names.named(CONFIG_KEY_OrcidClientSecret)).toInstance(
				context.getParameters().getFirst(CONFIG_KEY_OrcidClientSecret).getValue().toString());
		bind(String.class).annotatedWith(Names.named(CONFIG_KEY_OrcidReturnURI)).toInstance(
				context.getParameters().getFirst(CONFIG_KEY_OrcidReturnURI).getValue().toString());
		bind(Boolean.class).annotatedWith(Names.named(CONFIG_KEY_OrcidSandbox)).toInstance(
				Boolean.valueOf(context.getParameters().getFirst(CONFIG_KEY_OrcidSandbox).getValue().toString()));

		//TODO: - put in external config?
		bind(Integer.class).annotatedWith(Names.named(CONFIG_KEY_CacheTimeout)).toInstance(30);
		bind(Integer.class).annotatedWith(Names.named(CONFIG_KEY_CacheMaxsize)).toInstance(1000);
		
		// bind dependencies
		bind(OrcidOAuthClient.class).asEagerSingleton();
		bind(OrcidPublicClient.class).asEagerSingleton();
		bind(CacheManager.class).asEagerSingleton();
		bind(IsOrcidWorkProvider.class).to(provider.getClass());
	}

}
