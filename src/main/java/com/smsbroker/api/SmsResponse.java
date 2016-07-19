package com.smsbroker.api;

import java.util.Date;

/**
 * Created by xonda on 7/18/16.
 */
public class SmsResponse {
    private String status;
    private Date time;

    public SmsResponse(String status) {
        this.status = status;
        this.time = new Date();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
