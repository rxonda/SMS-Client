package com.smsbroker.provider;

/**
 * Created by xonda on 7/18/16.
 */
class RestRequestObject {
    Integer id;
    String from;
    String to;
    String body;

    public RestRequestObject(Integer id, String from, String to, String body) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.body = body;
    }

    public Integer getId() {
        return id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getBody() {
        return body;
    }
}
