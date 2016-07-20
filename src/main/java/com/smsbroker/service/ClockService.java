package com.smsbroker.service;

import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by xonda on 7/19/16.
 */
@Service
public class ClockService {
    public Date now() {
        return new Date();
    }
}
