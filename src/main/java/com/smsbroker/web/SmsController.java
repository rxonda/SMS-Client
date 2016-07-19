package com.smsbroker.web;

import com.smsbroker.api.SmsRequest;
import com.smsbroker.api.SmsResponse;
import com.smsbroker.api.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import rx.schedulers.Schedulers;

import java.util.concurrent.ExecutorService;

/**
 * Created by xonda on 7/18/16.
 */
@RestController
@RequestMapping("/api/v1")
public class SmsController {
    @Autowired
    private ExecutorService executorService;

    @Autowired
    private SmsService smsService;

    @RequestMapping(value = "/sms", method = RequestMethod.POST)
    public DeferredResult<SmsResponse> sendMessage(@RequestBody SmsRequest request) {
        final DeferredResult<SmsResponse> result = new DeferredResult<>();

        smsService.send(request)
                .subscribeOn(Schedulers.from(executorService))
                .subscribe(result::setResult, result::setErrorResult);

        return result;
    }
}
