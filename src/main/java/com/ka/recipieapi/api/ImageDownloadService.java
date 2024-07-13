package com.ka.recipieapi.api;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class ImageDownloadService {

    private final RestTemplate restTemplate;

    public ImageDownloadService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public File downloadImage(String imageUrl, String destinationFilePath) throws Exception {
        // Make HTTP GET request to fetch image
        ResponseEntity<byte[]> response = restTemplate.getForEntity(imageUrl, byte[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            // Convert response to byte array and save to file
            byte[] imageBytes = response.getBody();
            Path destinationPath = Paths.get(destinationFilePath);
            Files.write(destinationPath, imageBytes);
            return destinationPath.toFile();
        } else {
            throw new Exception("Failed to download image from URL: " + imageUrl);
        }
    }
}
