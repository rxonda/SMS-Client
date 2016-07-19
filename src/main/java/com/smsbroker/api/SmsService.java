package com.smsbroker.api;

import rx.Observable;

/**
 * Created by xonda on 7/18/16.
 */
public interface SmsService {
    Observable<SmsResponse> send(SmsRequest request);
}
