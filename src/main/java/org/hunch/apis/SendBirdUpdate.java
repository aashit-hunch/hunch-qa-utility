package org.hunch.apis;

import org.hunch.constants.Config;
import org.hunch.core.AbstractApiClient;
import org.hunch.core.HttpMethodType;
import org.hunch.utils.CryptoUtility;

public class SendBirdUpdate extends AbstractApiClient {

    public SendBirdUpdate(String userId){
        //https://api-9FFCB981-7B58-45B4-8FAE-9E546BB1C375.sendbird.com/v3/users/
        super("https://api-"+ CryptoUtility.decrypt(Config.SENDBIRD_API_URI) +".sendbird.com/v3/users/"+userId, HttpMethodType.PUT);
        addHeader("Api-Token", CryptoUtility.decrypt(Config.SENDBIRD_API_KEY));
    }
}
