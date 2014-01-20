package uk.bl.odin.orcid.rest;

import java.util.ArrayList;
import java.util.Calendar;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.CacheDirective;
import org.restlet.data.Status;
import org.restlet.routing.Filter;

/** Filter that modifies max age and expiration date HTTP headers to expire in a
 * years time.
 */
public class CacheFilter extends Filter {

	public CacheFilter(Context context, Restlet restlet) {
		super(context, restlet);
	}

	/** Sets the HTTP Headers if request was successful
	 */
	protected void afterHandle(Request request, Response response) {
		super.afterHandle(request, response);
		if (response != null && response.getEntity() != null) {
			if (response.getStatus().equals(Status.SUCCESS_OK)) {
				final Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.YEAR, 1);
				response.getEntity().setExpirationDate(calendar.getTime());
				response.setCacheDirectives(new ArrayList<CacheDirective>());
				response.getCacheDirectives().add(CacheDirective.maxAge(31536000));
			}
		}
	}

}
