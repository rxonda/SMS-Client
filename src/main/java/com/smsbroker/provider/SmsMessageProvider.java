package com.smsbroker.provider;

import com.smsbroker.api.SmsRequest;
import com.smsbroker.api.SmsResponse;
import com.smsbroker.api.SmsService;
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
 * Created by xonda on 7/17/16.
 */
@Service
public class SmsMessageProvider implements SmsService {

    @Value("${config.url}")
    private String url;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public Observable<SmsResponse> send(SmsRequest request) {
        return Observable.<SmsResponse>create(subscriber -> {
            try {
                URI uri = new URI("http://" + url);
                HttpEntity<RestRequestObject> requestUpdate = new HttpEntity(new RestRequestObject(0, "automatic messaging system", request.getAddress(), request.getText()));
                ResponseEntity responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, requestUpdate, Void.class);

                if(responseEntity.getStatusCode() == HttpStatus.OK) {
                    subscriber.onNext(new SmsResponse("Foi tudo ok!"));
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
