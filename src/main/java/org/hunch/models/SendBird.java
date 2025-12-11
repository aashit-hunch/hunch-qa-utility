package org.hunch.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendBird {
    private String user_id;
    private String nickname;
    private String profile_url;
}
