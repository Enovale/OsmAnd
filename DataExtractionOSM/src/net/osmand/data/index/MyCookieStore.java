package net.osmand.data.index;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

public class MyCookieStore implements CookieStore {

	private final CookieStore ***REMOVED***Store;

	public MyCookieStore(CookieStore ***REMOVED***Store) {
		this.***REMOVED***Store = ***REMOVED***Store;
	}

	@Override
	public void add(URI uri, HttpCookie ***REMOVED***) {
		***REMOVED***Store.add(uri, ***REMOVED***);
	}

	@Override
	public List<HttpCookie> get(URI uri) {
		return ***REMOVED***Store.get(uri);
	}

	@Override
	public List<HttpCookie> getCookies() {
		return ***REMOVED***Store.getCookies();
	}

	@Override
	public List<URI> getURIs() {
		return ***REMOVED***Store.getURIs();
	}

	@Override
	public boolean remove(URI uri, HttpCookie ***REMOVED***) {
		return ***REMOVED***Store.remove(uri, ***REMOVED***);
	}

	@Override
	public boolean removeAll() {
		return ***REMOVED***Store.removeAll();
	}

	public String getCookie(String key) {
		for (HttpCookie c : ***REMOVED***Store.getCookies()) {
			if (c.getName().equals(key)) {
				return c.getValue();
			}
		}
		return null;
	}
}
