package org.hunch.apis;

import org.hunch.constants.Config;
import org.hunch.core.AbstractApiClient;
import org.hunch.core.HttpMethodType;
import org.hunch.utils.CryptoUtility;

public class SendBirdCreate extends AbstractApiClient {

    public SendBirdCreate(){
        super("https://api-"+ CryptoUtility.decrypt(Config.SENDBIRD_API_URI) +".sendbird.com/v3/users/", HttpMethodType.POST);
        addHeader("Api-Token", CryptoUtility.decrypt(Config.SENDBIRD_API_KEY));
    }
}
