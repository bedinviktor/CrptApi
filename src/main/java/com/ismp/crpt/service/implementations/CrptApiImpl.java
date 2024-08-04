package com.ismp.crpt.service.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ismp.crpt.service.interfaces.CrptApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Document;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CrptApiImpl implements CrptApi {
    private final Semaphore semaphore;
    private final long intervalMillis;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${crptapi.url}")
    private String apiUrl;

    public CrptApiImpl(TimeUnit timeUnit, int requestLimit) {
        this.semaphore = new Semaphore(requestLimit);
        this.intervalMillis = timeUnit.toMillis(1);
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void createDocument(Document document, String signature) {
        try {
            semaphore.acquire();
            try {
                String jsonDocument = objectMapper.writeValueAsString(document);
                HttpRequest request = buildHttpRequest(jsonDocument, signature);

                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            } finally {
                new Thread(() -> {
                    try {
                        Thread.sleep(intervalMillis);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        semaphore.release();
                    }
                }).start();
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке файла ", e);
        }
    }

    private HttpRequest buildHttpRequest(String jsonDocument, String signature) {
        return HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Signature", signature)
                .POST(HttpRequest.BodyPublishers.ofString(jsonDocument))
                .build();
    }
}