package org.hunch.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hunch.core.MimeType;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetPreSignedUrl {
    private String type;
    private String fileName;
    private String contentType;
    private String draftId;

    public GetPreSignedUrl(String type, String fileName, MimeType contentType, String draftId) {
        this.fileName = fileName;
        this.contentType = contentType.getValue();
        this.draftId = draftId;
        this.type = type;
    }
}
