package com.smsbroker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xonda on 7/19/16.
 */
@Configuration
public class AppConfiguration {

    @Bean
    public ExecutorService executorService(@Value("${config.threadpool.size}") int size) {
        return Executors.newFixedThreadPool(size);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
