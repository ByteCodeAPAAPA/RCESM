package com.example.rces.service.impl;

import com.example.rces.service.TokenService;
import com.jayway.jsonpath.JsonPath;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
@Transactional(transactionManager = "primaryTransactionManager")
public class TokenServiceImpl implements TokenService {

    private static final String TOKEN_URL = "http://localhost:8080/oauth/token";
    private static final String CLIENT_CREDENTIALS = "c3RkLWNsaWVudDo3NDA3MjE=";

    @Override
    public String getToken(String username, String password) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String formData = "username=" + URLEncoder.encode(username, StandardCharsets.UTF_8)
                + "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8)
                + "&grant_type=password";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_URL))
                .header("Accept", "application/json")
                .header("Authorization", "Basic " + CLIENT_CREDENTIALS)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String jsonBody = response.body();
            return JsonPath.read(jsonBody, "$.access_token");
        } else {
            throw new RuntimeException("Не удалось получить токен: " + response.body());
        }
    }
}
