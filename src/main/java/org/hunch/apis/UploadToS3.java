package org.hunch.apis;

import org.hunch.core.AbstractApiClient;
import org.hunch.core.HttpMethodType;
import org.hunch.core.MimeType;

public class UploadToS3 extends AbstractApiClient {
    public UploadToS3(String presignedUrl, MimeType contentType) {
        super(presignedUrl, HttpMethodType.PUT);
        addHeader("Content-Type", contentType.getValue());
        isURLEncoded(false);


    }
}
