package com.smsbroker.api;

import java.util.Date;

/**
 * Created by xonda on 7/18/16.
 */
public class SmsRequest {
    private String address;
    private String text;
    private Date expires;

    public SmsRequest() {
    }

    public SmsRequest(String address, String text, Date expires) {
        this.address = address;
        this.text = text;
        this.expires = expires;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }
}
