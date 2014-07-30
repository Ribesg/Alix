/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.internal.network.ssl;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class SSLSocketFactory {

   /**
    * Creates a Trusting SSL Socket: accepts any certificate.
    * Use with caution.
    *
    * @param url  the url to connect to
    * @param port the port to connect to
    *
    * @return a SSL Socket
    *
    * @throws SSLException if something went wrong
    */
   public static Socket getTrustingSSLSocket(final String url, final int port) throws SSLException {
      try {
         final SSLContext sslContext = SSLContext.getInstance("SSL");
         sslContext.init(null, new TrustManager[] {
            new X509TrustManager() {

               @Override
               public void checkClientTrusted(final X509Certificate[] x509Certificates, final String s) throws CertificateException {
                  // No Exception = Accept all
               }

               @Override
               public void checkServerTrusted(final X509Certificate[] x509Certificates, final String s) throws CertificateException {
                  // No Exception = Accept all
               }

               @Override
               public X509Certificate[] getAcceptedIssuers() {
                  return null;
               }
            }
         }, null);
         final javax.net.ssl.SSLSocketFactory factory = sslContext.getSocketFactory();
         final SSLSocket resultSocket = (SSLSocket) factory.createSocket(url, port);

         // Disable DHE because it doesn't work for some reason
         final List<String> ciphers = new ArrayList<>();
         for (final String s : resultSocket.getEnabledCipherSuites()) {
            if (!s.contains("DHE")) {
               ciphers.add(s);
            }
         }
         resultSocket.setEnabledCipherSuites(ciphers.toArray(new String[ciphers.size()]));

         resultSocket.startHandshake();
         return resultSocket;
      } catch (Exception e) {
         throw new SSLException("Failed to create SSL socket", e);
      }
   }

   /**
    * Creates a Secured SSL Socket
    *
    * @param url  the url to connect to
    * @param port the port to connect to
    *
    * @return a SSL Socket
    *
    * @throws SSLException if something went wrong
    */
   public static Socket getSecuredSSLSocket(final String url, final int port) throws SSLException {
      throw new UnsupportedOperationException("Not yet implemented"); // TODO
   }

}
