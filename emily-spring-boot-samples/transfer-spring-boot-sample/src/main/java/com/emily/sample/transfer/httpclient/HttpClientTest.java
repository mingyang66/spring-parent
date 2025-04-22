package com.emily.sample.transfer.httpclient;

import java.net.Authenticator;
import java.net.http.HttpClient;
import java.time.Duration;

/**
 * @author :  Emily
 * @since :  2025/1/6 下午1:56
 */
public class HttpClientTest {
    public static void main(String[] args) {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .authenticator(Authenticator.getDefault())
                .build();

    }
}
