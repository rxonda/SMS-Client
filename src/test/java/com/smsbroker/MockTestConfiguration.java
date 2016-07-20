package com.smsbroker;

import com.smsbroker.service.ClockService;
import com.smsbroker.service.SmsRestClientService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Created by xonda on 7/19/16.
 */
@Profile("test")
@Configuration
public class MockTestConfiguration {
    @Bean
    @Primary
    public SmsRestClientService smsRestClientService() {
        return Mockito.mock(SmsRestClientService.class);
    }

    @Bean
    @Primary
    public ClockService clockService() {
        return Mockito.mock(ClockService.class);
    }
}
