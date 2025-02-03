import javax.net.ssl.*;
import java.net.http.*;
import java.net.URI;
import java.nio.file.*;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

public class HttpClientWithMutualTLS {
    public static void main(String[] args) throws Exception {
        String url = "https://your-api-endpoint.com";
        String payload = "{\"key\": \"value\"}";

        // Load CA Certificate
        String caCertificatePath = "path/to/ca_certificate.pem";
        X509Certificate caCert = loadCertificate(caCertificatePath);

        // Load User Certificate & Private Key
        String userCertPath = "path/to/fa_user_certificate.pem";
        String privateKeyPath = "path/to/fa_user_private_key.pem";
        X509Certificate userCert = loadCertificate(userCertPath);
        PrivateKey privateKey = loadPrivateKey(privateKeyPath);

        // Create KeyStore and TrustStore
        KeyStore keyStore = createKeyStore(userCert, privateKey);
        KeyStore trustStore = createTrustStore(caCert);

        // Configure SSLContext
        SSLContext sslContext = createSSLContext(keyStore, trustStore);

        // Create HTTP Client with SSL
        HttpClient client = HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();

        // Create HTTP Request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        // Send Request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
    }

    // Helper to Load Certificate
    private static X509Certificate loadCertificate(String path) throws Exception {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        try (var in = Files.newInputStream(Paths.get(path))) {
            return (X509Certificate) factory.generateCertificate(in);
        }
    }

    // Helper to Load Private Key
    private static PrivateKey loadPrivateKey(String path) throws Exception {
        // This assumes the private key is in PKCS#8 format.
        byte[] keyBytes = Files.readAllBytes(Paths.get(path));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    // Create KeyStore
    private static KeyStore createKeyStore(X509Certificate cert, PrivateKey key) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);
        keyStore.setKeyEntry("user-key", key, "changeit".toCharArray(), new java.security.cert.Certificate[]{cert});
        return keyStore;
    }

    // Create TrustStore
    private static KeyStore createTrustStore(X509Certificate caCert) throws Exception {
        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        trustStore.load(null, null);
        trustStore.setCertificateEntry("ca-cert", caCert);
        return trustStore;
    }

    // Create SSLContext
    private static SSLContext createSSLContext(KeyStore keyStore, KeyStore trustStore) throws Exception {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, "changeit".toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        return sslContext;
    }
}
