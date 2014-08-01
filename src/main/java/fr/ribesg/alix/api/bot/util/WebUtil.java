/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.api.bot.util;

import fr.ribesg.alix.api.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class WebUtil {

   private static final String URL_SHORTENER_URL = "http://is.gd/create.php?format=simple&url=";

   private static final int DEFAULT_TIMEOUT = 5_000;

   private static final Map<String, String> DEFAULT_HEADERS;

   static {
      final Map<String, String> defaultHeaders = new HashMap<>();
      defaultHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
      DEFAULT_HEADERS = Collections.unmodifiableMap(defaultHeaders);
   }

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
    * Parses a web page content as a Jsoup Document.
    *
    * @param documentContent the content of the web page in a single String
    *
    * @return the web page as a Jsoup Document
    */
   public static Document parseHtml(final String documentContent) {
      return Jsoup.parse(documentContent);
   }

   /**
    * Parses an XML document as a Jsoup Document.
    *
    * @param documentContent the XML document
    *
    * @return the XML document as a Jsoup Document
    */
   public static Document parseXml(final String documentContent) {
      return Jsoup.parse(documentContent, "", Parser.xmlParser());
   }

   /**
    * GET a web resource as String.
    * Can also get stuff like Json content from some APIs, etc.
    *
    * @param urlString the URL of the web resource
    *
    * @return the web resource in a single String
    *
    * @throws IOException if something fails
    */
   public static String get(final String urlString) throws IOException {
      return get(urlString, DEFAULT_TIMEOUT, null);
   }

   /**
    * GET a web resource as String.
    * Can also get stuff like Json content from some APIs, etc.
    *
    * @param urlString the URL of the web resource
    * @param timeOut   maximum time to wait for the web server
    *
    * @return the web resource as a single String
    *
    * @throws IOException if something fails
    */
   public static String get(final String urlString, final int timeOut) throws IOException {
      return get(urlString, timeOut, null);
   }

   /**
    * GET a web resource as String.
    * Can also get stuff like Json content from some APIs, etc.
    *
    * @param urlString   the URL of the web resource
    * @param httpHeaders additional HTTP Headers
    *
    * @return the web resource as a single String
    *
    * @throws IOException if something fails
    */
   public static String get(final String urlString, final Map<String, String> httpHeaders) throws IOException {
      return get(urlString, DEFAULT_TIMEOUT, httpHeaders);
   }

   /**
    * GET a web resource as String.
    * Can also get stuff like Json content from some APIs, etc.
    *
    * @param urlString   the URL of the web resource
    * @param timeOut     maximum time to wait for the web server
    * @param httpHeaders additional HTTP Headers
    *
    * @return the web resource in a single String
    *
    * @throws IOException if something fails
    */
   public static String get(final String urlString, final int timeOut, final Map<String, String> httpHeaders) throws IOException {
      Log.debug("Getting page " + urlString + " ...");

      final URL url = new URL(urlString);

      final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      DEFAULT_HEADERS.forEach(connection::setRequestProperty);
      if (httpHeaders != null) {
         httpHeaders.forEach(connection::setRequestProperty);
      }

      connection.setConnectTimeout(timeOut);
      connection.setReadTimeout(timeOut);
      connection.setUseCaches(false);

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

   /**
    * Sends a POST request to web resource and get response as String.
    *
    * @param urlString   the URL of the web resource
    * @param contentType the Content-Type of the POST request
    * @param postData    the data of the POST request
    *
    * @return the response in a single String
    *
    * @throws IOException if something fails
    */
   public static String post(final String urlString, final String contentType, final String postData) throws IOException {
      return post(urlString, DEFAULT_TIMEOUT, contentType, postData, null);
   }

   /**
    * Sends a POST request to web resource and get response as String.
    *
    * @param urlString   the URL of the web resource
    * @param timeOut     maximum time to wait for the web server
    * @param contentType the Content-Type of the POST request
    * @param postData    the data of the POST request
    *
    * @return the response in a single String
    *
    * @throws IOException if something fails
    */
   public static String post(final String urlString, final int timeOut, final String contentType, final String postData) throws IOException {
      return post(urlString, timeOut, contentType, postData, null);
   }

   /**
    * Sends a POST request to web resource and get response as String.
    *
    * @param urlString   the URL of the web resource
    * @param contentType the Content-Type of the POST request
    * @param postData    the data of the POST request
    * @param httpHeaders additional HTTP Headers
    *
    * @return the response in a single String
    *
    * @throws IOException if something fails
    */
   public static String post(final String urlString, final String contentType, final String postData, final Map<String, String> httpHeaders) throws IOException {
      return post(urlString, DEFAULT_TIMEOUT, contentType, postData, null);
   }

   /**
    * Sends a POST request to web resource and get response as String.
    *
    * @param urlString   the URL of the web resource
    * @param timeOut     maximum time to wait for the web server
    * @param contentType the Content-Type of the POST request
    * @param postData    the data of the POST request
    * @param httpHeaders additional HTTP Headers
    *
    * @return the response in a single String
    *
    * @throws IOException if something fails
    */
   public static String post(final String urlString, final int timeOut, final String contentType, final String postData, final Map<String, String> httpHeaders) throws IOException {
      Log.debug("Sending POST to " + urlString + " ...");

      final URL url = new URL(urlString);

      final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      connection.setRequestProperty("Content-Type", contentType);
      DEFAULT_HEADERS.forEach(connection::setRequestProperty);
      if (httpHeaders != null) {
         httpHeaders.forEach(connection::setRequestProperty);
      }

      connection.setConnectTimeout(timeOut);
      connection.setReadTimeout(timeOut);
      connection.setUseCaches(false);
      connection.setDoOutput(true);

      connection.getOutputStream().write(postData.getBytes());
      connection.getOutputStream().flush();
      connection.getOutputStream().close();

      try (final BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
         final StringBuilder buffer = new StringBuilder();
         String line;
         while ((line = input.readLine()) != null) {
            buffer.append(line);
            buffer.append('\n');
         }

         Log.debug("Done sending POST to " + urlString + " !");
         return buffer.toString();
      }
   }
}
