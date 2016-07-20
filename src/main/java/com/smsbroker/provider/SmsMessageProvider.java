package com.smsbroker.provider;

import com.smsbroker.api.SmsRequest;
import com.smsbroker.api.SmsResponse;
import com.smsbroker.api.SmsService;
import com.smsbroker.service.ClockService;
import com.smsbroker.service.RestRequestObject;
import com.smsbroker.service.SmsRestClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;

/**
 * Created by xonda on 7/17/16.
 */
@Service
public class SmsMessageProvider implements SmsService {

    @Autowired
    private SmsRestClientService smsRestClientService;

    @Autowired
    private ClockService clockService;

    @Override
    public Observable<SmsResponse> send(SmsRequest request) {
        return Observable.<SmsResponse>create(subscriber -> {
            if(clockService.now().after(request.getExpires())) {
                subscriber.onError(new IllegalArgumentException("Data expirada"));
                return;
            }

            smsRestClientService.exchange(new RestRequestObject(0, "automatic messaging system", request.getAddress(), request.getText()))
                    .subscribe(status ->{
                        subscriber.onNext(new SmsResponse(status));
                        subscriber.onCompleted();
                    }, t -> subscriber.onError(t));
        });
    }
}
