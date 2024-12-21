import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
public class FileController {

    private final WebClient webClient;

    public FileController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @GetMapping("/extract-zip")
    public ResponseEntity<String> extractZipFile() {
        try {
            // Step 1: Download the ZIP file
            byte[] zipFileContent = webClient
                    .get()
                    .uri("https://example.com/api/get-zip-file") // Replace with your API URL
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            if (zipFileContent == null) {
                return ResponseEntity.badRequest().body("Failed to download ZIP file.");
            }

            // Step 2: Extract the ZIP file
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(zipFileContent);
            ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream);
            ZipEntry entry;
            StringBuilder extractedContent = new StringBuilder();

            while ((entry = zipInputStream.getNextEntry()) != null) {
                // Process files based on your selection criteria
                if (!entry.isDirectory() && entry.getName().endsWith(".txt")) { // Example: Select .txt files
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zipInputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, len);
                    }
                    extractedContent.append("File: ").append(entry.getName()).append("\n")
                            .append(new String(outputStream.toByteArray(), StandardCharsets.UTF_8))
                            .append("\n\n");
                }
            }

            zipInputStream.close();

            // Step 3: Send the content in the response
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(extractedContent.toString());

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error processing ZIP file: " + e.getMessage());
        }
    }
}
