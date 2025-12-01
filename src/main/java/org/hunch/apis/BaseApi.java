package org.hunch.apis;

import org.hunch.constants.Config;
import org.hunch.core.AbstractApiClient;
import org.hunch.core.HttpMethodType;
import org.hunch.core.MimeType;

public class BaseApi extends AbstractApiClient {

    public BaseApi(){
        super(Config.BASE_URL, HttpMethodType.POST);
        addHeader("content-type", MimeType.APPLICATION_JSON.getValue());
    }
}
