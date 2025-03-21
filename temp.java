import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.net.ssl.*;

import org.json.JSONObject;

public class AccessTokenClient {
    private static final String ISGA_HOST = "https://isga-sts.gs-go-direct.ubsdev.net";
    private static final String NUCLEUS_ISGA_SCOPE = "https://openai-nucleus-dev.ubsdev.net/nucleus";
    private static final String USER_CERT_PATH = "./certs/nucleus_user.crt";
    private static final String USER_KEY_PATH = "./certs/FA0CBGR_NP.key";
    private static final String CA_CERT_PATH = "./certs/FA0CBGR.crt";

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .sslContext(createSSLContext())
                .build();

        JSONObject requestBody = new JSONObject();
        requestBody.put("grant_type", "client_credentials");
        requestBody.put("scope", NUCLEUS_ISGA_SCOPE);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ISGA_HOST + "/auth/oauth/v3/token"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject responseJson = new JSONObject(response.body());
            String accessToken = responseJson.optString("access_token", null);

            if (accessToken != null) {
                System.out.println("Access Token: " + accessToken);
            } else {
                System.err.println("Access token not found in response.");
            }
        } else {
            System.err.println("HTTP Error: " + response.statusCode());
            System.err.println(response.body());
        }
    }

    private static SSLContext createSSLContext() throws Exception {
        // Load CA certificate
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        Certificate caCert = certFactory.generateCertificate(Files.newInputStream(Paths.get(CA_CERT_PATH)));

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("caCert", caCert);

        // Load user certificate
        Certificate userCert = certFactory.generateCertificate(Files.newInputStream(Paths.get(USER_CERT_PATH)));
        keyStore.setCertificateEntry("userCert", userCert);

        // Load private key
        PrivateKey privateKey = loadPrivateKey(USER_KEY_PATH);
        keyStore.setKeyEntry("userKey", privateKey, new char[0], new Certificate[]{userCert});

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, new char[0]);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return sslContext;
    }

    private static PrivateKey loadPrivateKey(String keyPath) throws Exception {
        String keyContent = new String(Files.readAllBytes(Paths.get(keyPath)))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(keyContent);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(keySpec);
    }
}
