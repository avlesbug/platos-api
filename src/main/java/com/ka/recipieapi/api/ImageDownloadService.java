package com.ka.recipieapi.api;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

@Service
public class ImageDownloadService {

    public File downloadImage(String imageUrl, String destinationFilePath) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(imageUrl))
            .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() == 200) {
            byte[] imageBytes = response.body();
            Path destinationPath = Paths.get(destinationFilePath);
            Files.write(destinationPath, imageBytes);
            return destinationPath.toFile();
        } else {
            throw new Exception("Failed to download image from URL: " + imageUrl);
        }
    }
}
