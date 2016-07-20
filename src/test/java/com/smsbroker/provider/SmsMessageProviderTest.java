package com.smsbroker.provider;

import com.smsbroker.Application;
import com.smsbroker.api.SmsRequest;
import com.smsbroker.api.SmsResponse;
import com.smsbroker.repository.Sms;
import com.smsbroker.repository.SmsRepository;
import com.smsbroker.service.ClockService;
import com.smsbroker.service.RestRequestObject;
import com.smsbroker.service.SmsRestClientService;
import org.junit.After;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Autowired
    private SmsRepository smsRepository;

    @After
    public void cleanup() {
        smsRepository.deleteAll();
    }

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
        List<Sms> smses = new ArrayList<>();
        smsRepository.findAll().forEach(sms -> {
            smses.add(sms);
            Assert.assertEquals("O status deve ser ", "delivered", sms.getStatus());
        });

        Assert.assertEquals("Deve ter apenas 1 sms", 1, smses.size());
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
        List smses = new ArrayList<>();
        smsRepository.findAll().forEach( sms -> {
            smses.add(sms);
        });

        Assert.assertTrue("Nao deve ter smss", smses.isEmpty());
    }

    @Test
    public void shouldNotSendMessagesBiggerThan160Characters() {
        when(smsRestClientService.exchange(any(RestRequestObject.class))).thenReturn(Observable.empty());
        when(clockService.now()).thenReturn(parse("18/07/2016"));

        Subscriber<SmsResponse> mockSubscriber = mock(Subscriber.class);
        TestSubscriber<SmsResponse> subscriber = new TestSubscriber(mockSubscriber);

        Date expired = parse("19/07/2016");

        smsMessageProvider.send(new SmsRequest("endereco", "This is a very long text to achieve a validation required for telecom companies not shutdown doe to amount of text messages and there exagerated lenght post by their dear customers!", expired))
                .subscribe(subscriber);

        subscriber.assertError(IllegalArgumentException.class);

        subscriber.assertNoValues();
        subscriber.assertNotCompleted();
        List smses = new ArrayList<>();
        smsRepository.findAll().forEach( sms -> {
            smses.add(sms);
        });

        Assert.assertTrue("Nao deve ter smss", smses.isEmpty());
    }

    @Test
    public void shouldRecordErrorOnFailedToDeliver() {
        when(smsRestClientService.exchange(any(RestRequestObject.class))).thenReturn(Observable.error(new RuntimeException("some error")));
        when(clockService.now()).thenReturn(parse("18/07/2016"));

        Subscriber<SmsResponse> mockSubscriber = mock(Subscriber.class);
        TestSubscriber<SmsResponse> subscriber = new TestSubscriber(mockSubscriber);

        Date expired = parse("19/07/2016");

        smsMessageProvider.send(new SmsRequest("endereco", "hello you my friend!", expired))
                .subscribe(subscriber);

        subscriber.assertError(RuntimeException.class);

        subscriber.assertNoValues();
        subscriber.assertNotCompleted();
        List smses = new ArrayList<>();
        smsRepository.findAll().forEach( sms -> {
            smses.add(sms);
            Assert.assertEquals("O status deve ser ", "Fail to deliver", sms.getStatus());
        });

        Assert.assertEquals("Deve ter apenas 1 sms", 1, smses.size());
    }
}