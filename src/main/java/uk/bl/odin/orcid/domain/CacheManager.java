package uk.bl.odin.orcid.domain;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import uk.bl.odin.orcid.client.SearchKey;
import uk.bl.odin.orcid.schema.messages.onepointtwo.OrcidProfile;
import uk.bl.odin.orcid.schema.messages.onepointtwo.OrcidSearchResults;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Default cacheManager. Uses Guava cache
 * 
 * TODO: refactor into interface so we can swap memcached in/out using Guice if
 * required.
 * 
 * @author tom
 * 
 */
public class CacheManager {

	private final Cache<SearchKey, OrcidSearchResults> searchCache;

	private final Cache<String, OrcidProfile> profileCache;

	@Inject
	public CacheManager(@Named("OrcidCacheTimeout") Integer timeOutInMinutes,
			@Named("OrcidCacheMaxsize") Integer maximumSize) {
		searchCache = CacheBuilder.newBuilder().expireAfterWrite(timeOutInMinutes, TimeUnit.MINUTES)
				.maximumSize(maximumSize).build();
		profileCache = CacheBuilder.newBuilder().expireAfterWrite(timeOutInMinutes, TimeUnit.MINUTES)
				.maximumSize(maximumSize).build();
	}

	public Cache<SearchKey, OrcidSearchResults> getSearchCache() {
		return searchCache;
	}

	public Cache<String, OrcidProfile> getProfilecache() {
		return profileCache;
	}

}
