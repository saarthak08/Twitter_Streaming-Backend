package com.sg.twitterstreaming.service;

import com.sg.twitterstreaming.config.Key;
import com.sg.twitterstreaming.model.tweet.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Service
public class TweetAPIService {

    private final RestTemplate restTemplate;

    private final WebClient webClient;

    private final String token = Key.BearerToken;

    private final String baseSearchURL = "https://api.twitter.com/2/tweets/search/recent?max_results=20&tweet.fields=created_at,public_metrics&";
    private final String baseRuleURL = "https://api.twitter.com/2/tweets/search/stream/rules";

    @Autowired
    public TweetAPIService() {
        webClient = WebClient.create();
        restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException, IOException {
                httpRequest.getHeaders().add("Authorization", "Bearer " + token);
                return clientHttpRequestExecution.execute(httpRequest, bytes);
            }
        });
    }

    public ResponseEntity<?> recentSearchTweetsByKeyword(String keyword, String nextToken) {
        if (nextToken == null) {
            return restTemplate.getForEntity(baseSearchURL + "query={keyword}", Data.class, keyword);
        } else {
            return restTemplate.getForEntity(baseSearchURL + "query={keyword}&next_token={nextToken}", Data.class, keyword, nextToken);
        }
    }

    public Flux<String> startSampleTweetsStreaming() {
        return webClient.get()
                .uri("https://api.twitter.com/2/tweets/sample/stream?tweet.fields=created_at,public_metrics")
                .header("Authorization", "Bearer " + Key.BearerToken)
                .retrieve()
                .bodyToFlux(String.class)
                .delayElements(Duration.ofSeconds(1))
                .take(Duration.ofMinutes(1));
    }


    public Flux<String> startRealtimeTweetsStreaming() {
        return webClient.get()
                .uri("https://api.twitter.com/2/tweets/search/stream")
                .header("Authorization", "Bearer " + Key.BearerToken)
                .retrieve()
                .bodyToFlux(String.class)
                .delayElements(Duration.ofSeconds(1))
                .take(Duration.ofMinutes(1));
    }

    public ResponseEntity<?> getRealtimeStreamRules() {
        return restTemplate.getForEntity(baseRuleURL, com.sg.twitterstreaming.model.streamrule.Data.class);
    }

    public ResponseEntity<Object> postRealtimeStreamRules(Map<String, Object> requestObject) {
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<Map<String, Object>>(requestObject);
        return restTemplate.postForEntity(baseRuleURL, httpEntity, Object.class);
    }
}
