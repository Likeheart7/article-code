package com.chenx.httpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.http.HttpResponse.BodyHandlers.ofString;

public class HttpClientDemo {
    private static final Logger log = LoggerFactory.getLogger(HttpClientDemo.class);

    public static void main(String[] args) {
//        traditionalRequest();
        httpclient();
    }

    private static void httpclient() {
        try(HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://postman-echo.com/get/")).build();
            client.sendAsync(request, ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .join();
        }
    }

    private static void traditionalRequest() {
        try {
            URL url = new URL("https://www.baidu.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(2000);
            connection.setDoInput(true);
            connection.connect();
            int responseCode = connection.getResponseCode();
            log.info("{}", responseCode);
            if (responseCode == 200) {
                String contentType = connection.getHeaderField("Content-Type");
                log.info("{}", contentType);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
