package com.smsbroker.provider;

import com.smsbroker.Application;
import com.smsbroker.api.SmsRequest;
import com.smsbroker.api.SmsResponse;
import com.smsbroker.service.ClockService;
import com.smsbroker.service.RestRequestObject;
import com.smsbroker.service.SmsRestClientService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

import java.util.Date;

import static com.smsbroker.util.Util.parse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by xonda on 7/19/16.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class SmsMessageProviderTest {

    @Autowired
    private SmsRestClientService smsRestClientService;

    @Autowired
    private ClockService clockService;

    @Autowired
    private SmsMessageProvider smsMessageProvider;

    @Test
    public void shouldSendAValidSms() {
        when(smsRestClientService.exchange(any(RestRequestObject.class))).thenReturn(Observable.just("blast"));
        when(clockService.now()).thenReturn(parse("18/07/2016"));

        Subscriber<SmsResponse> mockSubscriber = mock(Subscriber.class);
        TestSubscriber<SmsResponse> subscriber = new TestSubscriber(mockSubscriber);

        smsMessageProvider.send(new SmsRequest("endereco", "hello you my friend!", parse("19/07/2016")))
                .subscribe(subscriber);

        subscriber.assertNoErrors();
        subscriber.assertCompleted();

        ArgumentCaptor<SmsResponse> smsResponseArgumentCaptor = ArgumentCaptor.forClass(SmsResponse.class);
        verify(mockSubscriber, times(1)).onNext(smsResponseArgumentCaptor.capture());

        Assert.assertEquals("deve ser igual a ", "blast", smsResponseArgumentCaptor.getValue().getStatus());
    }

    @Test
    public void shouldNotSendExpiredMessage() {
        when(smsRestClientService.exchange(any(RestRequestObject.class))).thenReturn(Observable.empty());
        when(clockService.now()).thenReturn(parse("20/07/2016"));

        Subscriber<SmsResponse> mockSubscriber = mock(Subscriber.class);
        TestSubscriber<SmsResponse> subscriber = new TestSubscriber(mockSubscriber);

        Date expired = parse("19/07/2016");

        smsMessageProvider.send(new SmsRequest("endereco", "hello you my friend!", expired))
                .subscribe(subscriber);

        subscriber.assertError(IllegalArgumentException.class);

        subscriber.assertNoValues();
        subscriber.assertNotCompleted();
    }
}