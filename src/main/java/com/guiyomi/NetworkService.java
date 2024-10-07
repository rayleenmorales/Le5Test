package com.guiyomi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class NetworkService {

    // Method for sending a POST request
    public String sendPostRequest(String urlString, String jsonInputString) {
        HttpURLConnection conn = null;
        try {
            URL url = URI.create(urlString).toURL();
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            // Write the request body to the connection
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read the response
            int responseCode = conn.getResponseCode();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream(),
                    StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                if (responseCode >= 200 && responseCode < 300) {
                    return response.toString(); // Success response
                } else {
                    return "Error: " + responseCode + " - " + response.toString(); // Error response
                }
            }
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    // Method for sending a GET request
    public String sendGetRequest(String urlString) {
        HttpURLConnection conn = null;
        try {
            URL url = URI.create(urlString).toURL();
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    responseCode == HttpURLConnection.HTTP_OK ? conn.getInputStream() : conn.getErrorStream(),
                    StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return response.toString(); // Success response
                } else {
                    return "Error: " + responseCode + " - " + response.toString(); // Error response
                }
            }
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
