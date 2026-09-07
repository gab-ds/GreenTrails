import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class HealthCheck {

  public static void main(String[] args) {
    try {
      String certPath = System.getenv("SSL_CERT_PATH");
      boolean useSsl = certPath != null && !certPath.isBlank();
      String protocol = useSsl ? "https" : "http";
      int port = useSsl ? 8443 : 8080;

      String urlStr = protocol + "://localhost:" + port + "/actuator/health";

      HttpURLConnection c = (HttpURLConnection) URI.create(urlStr).toURL().openConnection();
      c.setConnectTimeout(5000);
      c.setReadTimeout(5000);
      c.setRequestMethod("GET");

      if (c instanceof HttpsURLConnection httpsConn) {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);

        try (InputStream certStream = Files.newInputStream(Paths.get(certPath))) {
          ks.setCertificateEntry("ca", cf.generateCertificate(certStream));
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, tmf.getTrustManagers(), null);
        httpsConn.setSSLSocketFactory(ctx.getSocketFactory());
      }

      int responseCode = c.getResponseCode();
      c.disconnect();

      System.exit(responseCode == 200 ? 0 : 1);
    } catch (Exception e) {
      System.exit(1);
    }
  }
}
