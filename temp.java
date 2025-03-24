import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.Lookup;
import org.apache.hc.client5.http.config.RegistryBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.DefaultTlsStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Timeout;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.nio.file.Paths;
import java.security.KeyStore;

public class SecureRestTemplateJKS {

    public static RestTemplate createRestTemplate(String keystorePath, String keystorePassword) throws Exception {
        // Load JKS Keystore
        KeyStore keyStore = KeyStore.getInstance("JKS");
        File keyStoreFile = Paths.get(keystorePath).toFile();
        try (var keyStoreInputStream = java.nio.file.Files.newInputStream(keyStoreFile.toPath())) {
            keyStore.load(keyStoreInputStream, keystorePassword.toCharArray());
        }

        // Create SSLContext
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, keystorePassword.toCharArray()) // Client cert
                .loadTrustMaterial((chain, authType) -> true) // Trust all certs (for testing)
                .build();

        // Configure TLS strategy
        Lookup<org.apache.hc.client5.http.ssl.TlsStrategy> tlsStrategyLookup = RegistryBuilder.<org.apache.hc.client5.http.ssl.TlsStrategy>create()
                .register("https", new DefaultTlsStrategy(sslContext))
                .build();

        // Create Connection Manager with TlsStrategy
        PoolingAsyncClientConnectionManager connectionManager = PoolingAsyncClientConnectionManager.builder()
                .setTlsStrategyRegistry(tlsStrategyLookup)
                .setMaxTotal(50)
                .setDefaultMaxPerRoute(10)
                .setValidateAfterInactivity(TimeValue.ofSeconds(5))
                .build();

        // Create Apache HttpClient 5
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
