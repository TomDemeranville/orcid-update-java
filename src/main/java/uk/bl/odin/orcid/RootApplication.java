package uk.bl.odin.orcid;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;

import uk.bl.odin.orcid.guice.SelfInjectingServerResourceModule;

import com.google.inject.Guice;

/**
 * Boilerplate application. Initializes Router and GuiceModule.
 */
public class RootApplication extends Application {

	public RootApplication(Context context) {
		super(context);

	}

	@Override
	public Restlet createInboundRoot() {
		Guice.createInjector(new GuiceConfigModule(this.getContext()), new SelfInjectingServerResourceModule());
		return new RootRouter(this.getContext());
	}

}
