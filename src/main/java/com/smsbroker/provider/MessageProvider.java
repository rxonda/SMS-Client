package com.smsbroker.provider;

import java.util.concurrent.Future;

/**
 * Created by xonda on 7/17/16.
 */
public interface MessageProvider {
    Future<ProviderResponse> send(String message);
}
