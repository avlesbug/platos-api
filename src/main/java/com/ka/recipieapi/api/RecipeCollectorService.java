package com.ka.recipieapi.api;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.NoSuchElementException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.ka.recipieapi.types.RecipeDto;

@Service
public class RecipeCollectorService {

    public String getPlainTextFromUrl(String urlString) throws IOException, NoSuchAlgorithmException,
        KeyManagementException {
        // Create a trust manager that does not validate certificate chains
        trustManager();

        // Now you can access the URL
        Document doc = Jsoup.connect(urlString).get();
        return doc.text();
    }

    public String getImageWithAltFromMain(String urlString, String title) throws IOException, NoSuchAlgorithmException,
        KeyManagementException {
        trustManager();

        // Now you can access the URL
        Document doc = Jsoup.connect(urlString).get();

        // Select the main tag
        Element mainTag = doc.selectFirst("main");
        if (mainTag != null) {
            // First, search for an image with alt text exactly equal to the title
            Element imageWithExactAlt = mainTag.selectFirst("img[alt=" + title + "]");
            if (imageWithExactAlt != null) {
                return imageWithExactAlt.absUrl("src");
            }

            // If no exact match is found, search for images with alt text containing the title
            Element imageWithAlt = mainTag.selectFirst("img[alt*=" + title + "]");
            if (imageWithAlt != null) {
                return imageWithAlt.absUrl("src");
            }
        }

        return "NoImage";
    }

    public RecipeDto getRecipeFromUrl(String url) throws NoSuchAlgorithmException, KeyManagementException {
        trustManager();
        try {
            // Fetch the HTML document from the provided URL
            Document doc = Jsoup.connect(url).get();

            // Select all <script> tags with type "application/ld+json"
            Elements jsonLdScripts = doc.select("script[type=application/ld+json]");

            // Create an ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            // Iterate over the selected scripts
            for (Element script : jsonLdScripts) {
                // Parse the contents of the script tag as a JsonNode
                String jsonText = script.html();
                JsonNode jsonNode = objectMapper.readTree(jsonText);

                if (jsonNode.isArray()) {
                    // If the JSON node is an array, iterate over the elements
                    ArrayNode arrayNode = (ArrayNode) jsonNode;
                    for (JsonNode node : arrayNode) {
                        if (node.has("@type") && node.get("@type").asText().equals("Recipe")) {
                            // Map the JsonNode to the RecipeDto class
                            System.out.println(node);
                            return objectMapper.treeToValue(node, RecipeDto.class);
                        }
                    }
                } else {
                    // If the JSON node is a single object, check it directly
                    if (jsonNode.has("@type") && jsonNode.get("@type").asText().equals("Recipe")) {
                        // Map the JsonNode to the RecipeDto class
                        System.out.println(jsonNode);
                        return objectMapper.treeToValue(jsonNode, RecipeDto.class);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new NoSuchElementException();
    }

    private void trustManager() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
        };

        // Install the all-trusting trust manager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = (hostname, session) -> true;

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }
}
