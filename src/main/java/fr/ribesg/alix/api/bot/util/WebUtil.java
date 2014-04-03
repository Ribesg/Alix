package fr.ribesg.alix.api.bot.util;
import fr.ribesg.alix.api.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Scanner;

public class WebUtil {

	private static final String URL_SHORTENER_URL = "http://is.gd/create.php?format=simple&url=";

	private static final int DEFAULT_TIMEOUT = 5_000;

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

	/**
	 * Parse a web page as a Jsoup Document.
	 * <p>
	 * If no POST parameter is specified, a GET request will be used.
	 *
	 * @param urlString the URL of the web page
	 * @param postData  optional POST parameters
	 *
	 * @return the web page as a Jsoup Document
	 *
	 * @throws IOException if something fails
	 */
	public static Document getPage(final String urlString, final String... postData) throws IOException {
		return Jsoup.parse(getString(urlString, postData));
	}

	/**
	 * Parse a web page as a Jsoup Document.
	 * <p>
	 * If no POST parameter is specified, a GET request will be used.
	 *
	 * @param urlString the URL of the web page
	 * @param timeOut   maximum time to wait for the web server
	 * @param postData  optional POST parameters
	 *
	 * @return the web page as a Jsoup Document
	 *
	 * @throws IOException if something fails
	 */
	public static Document getPage(final String urlString, final int timeOut, final String... postData) throws IOException {
		return Jsoup.parse(getString(urlString, timeOut, postData));
	}

	/**
	 * Get a web ressource as String.
	 * Can also get stuff like Json content from some APIs, etc.
	 * <p>
	 * If no POST parameter is specified, a GET request will be used.
	 *
	 * @param urlString the URL of the web ressource
	 * @param postData  optional POST parameters
	 *
	 * @return the web ressource in a single String
	 *
	 * @throws IOException if something fails
	 */
	public static String getString(final String urlString, final String... postData) throws IOException {
		return getString(urlString, DEFAULT_TIMEOUT, postData);
	}

	/**
	 * Get a web ressource as String.
	 * Can also get stuff like Json content from some APIs, etc.
	 * <p>
	 * If no POST parameter is specified, a GET request will be used.
	 *
	 * @param urlString the URL of the web ressource
	 * @param timeOut   maximum time to wait for the web server
	 * @param postData  optional POST parameters
	 *
	 * @return the web ressource in a single String
	 *
	 * @throws IOException if something fails
	 */
	public static String getString(final String urlString, final int timeOut, final String... postData) throws IOException {
		Log.debug("Getting page " + urlString + " ...");

		final URL url = new URL(urlString);

		final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestProperty("User-Agent",
		                              "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, " +
		                              "like Gecko) Chrome/23.0.1271.95 Safari/537.11"
		                             );

		connection.setConnectTimeout(timeOut);
		connection.setReadTimeout(timeOut);
		connection.setUseCaches(false);

		if (postData.length > 0) {
			connection.setRequestMethod("POST");
			final StringBuilder data = new StringBuilder(postData[0]);
			for (int i = 1; i < postData.length; i++) {
				data.append('&').append(postData[i]);
			}
			final Writer writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(data.toString());
			writer.flush();
			writer.close();
		}

		try (final BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			final StringBuilder buffer = new StringBuilder();
			String line;
			while ((line = input.readLine()) != null) {
				buffer.append(line);
				buffer.append('\n');
			}

			Log.debug("Done getting page " + urlString + " !");
			return buffer.toString();
		}
	}
}
