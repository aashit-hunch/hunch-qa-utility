package org.hunch.apis;

import org.hunch.constants.Config;
import org.hunch.core.AbstractApiClient;
import org.hunch.core.HttpMethodType;
import org.hunch.core.MimeType;

public class BaseApi extends AbstractApiClient {

    public BaseApi(){
        super(Config.BASE_URL, HttpMethodType.POST);
        addHeader("content-type", MimeType.APPLICATION_JSON.getValue());
        addHeader("useragent", Config.USER_AGENT);
        addHeader("X-Forwarded-For","63.116.61.253");
        addHeader("ip","63.116.61.253");
    }
}
