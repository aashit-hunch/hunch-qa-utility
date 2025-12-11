package org.hunch.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendBirdMessage {
    private String message_type;
    private String user_id;
    private String message;
    private String origin;
    private boolean send_push;
}
