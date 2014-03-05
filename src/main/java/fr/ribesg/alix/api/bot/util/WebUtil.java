package fr.ribesg.alix.api.bot.util;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Scanner;

public class WebUtil {

	private static final String URL_SHORTENER_URL = "http://is.gd/create.php?format=simple&url=";

	/**
	 * Shorten an URL using the http://is.gd/ api.
	 *
	 * @param longUrl the URL to shorten
	 *
	 * @return a short URL linking to the provided long URL
	 *
	 * @throws IOException if something fails
	 */
	public static String shortenUrl(final String longUrl) throws IOException {
		return shortenUrl(longUrl, null);
	}

	/**
	 * Shorten an URL using the http://is.gd/ api, behind-a-proxy version.
	 *
	 * @param longUrl the URL to shorten
	 *
	 * @return a short URL linking to the provided long URL
	 *
	 * @throws IOException if something fails
	 */
	public static String shortenUrl(final String longUrl, final Proxy proxy) throws IOException {
		try {
			final URL url = new URL(URL_SHORTENER_URL + longUrl);
			final HttpURLConnection connection = (HttpURLConnection) (proxy == null ? url.openConnection() : url.openConnection(proxy));
			connection.setConnectTimeout(5_000);
			connection.setReadTimeout(5_000);
			connection.connect();
			final InputStream is = connection.getInputStream();
			final Scanner scanner = new Scanner(is).useDelimiter("\\A");
			if (scanner.hasNext()) {
				return scanner.next();
			} else {
				throw new IOException("Nothing to read");
			}
		} catch (final IOException e) {
			throw new IOException("Failed to shorten URL: " + e.getMessage(), e);
		}
	}
}
