package uk.bl.odin.orcid;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;

/** Boilerplate application
 */
public class RootApplication extends Application {

	public RootApplication(Context context) {
		super(context);
	}

	@Override
	public Restlet createInboundRoot() {
		return new RootRouter(this.getContext());
	}

}
