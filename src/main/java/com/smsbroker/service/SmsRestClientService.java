package com.smsbroker.service;

import com.smsbroker.api.SmsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by xonda on 7/19/16.
 */
@Service
public class SmsRestClientService {

    @Value("${config.url}")
    private String url;

    @Autowired
    private RestTemplate restTemplate;

    public Observable<String> exchange(RestRequestObject request) {
        return Observable.<String>create(subscriber -> {
            try {
                URI uri = new URI("http://" + url);

                HttpEntity<RestRequestObject> requestUpdate = new HttpEntity(request);
                ResponseEntity responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, requestUpdate, Void.class);
                if(responseEntity.getStatusCode() == HttpStatus.OK) {
                    subscriber.onNext("Foi tudo ok!");
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new RuntimeException("Couldn't get response"));
                }
            } catch (URISyntaxException e) {
                subscriber.onError(e);
            }
        });
    }
}
