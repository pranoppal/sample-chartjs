import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.Registry;
import org.apache.hc.client5.http.config.RegistryBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Timeout;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.nio.file.Paths;
import java.security.KeyStore;

public class SecureRestTemplateJKS {

    public static RestTemplate createRestTemplate(String keystorePath, String keystorePassword) throws Exception {
        // Load JKS Keystore (for client authentication)
        KeyStore keyStore = KeyStore.getInstance("JKS"); // Specify JKS instead of PKCS12
        File keyStoreFile = Paths.get(keystorePath).toFile();
        try (var keyStoreInputStream = java.nio.file.Files.newInputStream(keyStoreFile.toPath())) {
            keyStore.load(keyStoreInputStream, keystorePassword.toCharArray());
        }

        // Create SSLContext with the JKS keystore
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, keystorePassword.toCharArray()) // Client cert
                .loadTrustMaterial((chain, authType) -> true) // Trust all certs (not for production)
                .build();

        // Create SSLConnectionSocketFactory
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);

        // Configure connection manager
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslSocketFactory)
                .register("http", new PlainConnectionSocketFactory())
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setDefaultMaxPerRoute(10);
        connectionManager.setMaxTotal(50);

        // Create Apache HttpClient 5 with SSL
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultConnectionConfig(ConnectionConfig.custom()
                        .setSocketTimeout(Timeout.ofSeconds(30))
                        .build())
                .build();

        // Use HttpClient 5 with RestTemplate
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }

    public static void main(String[] args) {
        try {
            // Specify the JKS keystore file path and password
            String keystorePath = "/path/to/keystore.jks"; // Update this
            String keystorePassword = "your-keystore-password"; // Update this

            RestTemplate restTemplate = createRestTemplate(keystorePath, keystorePassword);

            // Make a secure API request
            String url = "https://secure-api.example.com/data";
            String response = restTemplate.getForObject(url, String.class);

            System.out.println("Response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
