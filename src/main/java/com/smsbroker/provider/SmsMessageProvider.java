package com.smsbroker.provider;

import com.smsbroker.api.SmsRequest;
import com.smsbroker.api.SmsResponse;
import com.smsbroker.api.SmsService;
import com.smsbroker.repository.Sms;
import com.smsbroker.repository.SmsRepository;
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
    private ClockService clockService;

    @Autowired
    private SmsRepository smsRepository;

    @Autowired
    private SmsRestClientService smsRestClientService;

    @Override
    public Observable<SmsResponse> send(SmsRequest request) {
        return Observable.<SmsResponse>create(subscriber -> {
            if(clockService.now().after(request.getExpires())) {
                subscriber.onError(new IllegalArgumentException("Data expirada"));
                return;
            }
            Sms newSms = new Sms();
            smsRepository.save(newSms);

            smsRestClientService.exchange(new RestRequestObject(newSms.getId(), "automatic messaging system", request.getAddress(), request.getText()))
                    .subscribe(status ->{
                        newSms.setDelivered(clockService.now());
                        newSms.setStatus("delivered");
                        smsRepository.save(newSms);
                        subscriber.onNext(new SmsResponse(status));
                        subscriber.onCompleted();
                    }, t -> {
                        newSms.setStatus("Fail to deliver");
                        smsRepository.save(newSms);
                        subscriber.onError(t);
                    });
        });
    }
}
