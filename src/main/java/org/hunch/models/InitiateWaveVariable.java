package org.hunch.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hunch.enums.WaveRequestTypeEnum;
import org.hunch.enums.WaveRequestedFromEnum;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InitiateWaveVariable {

    private Input input;

    public  InitiateWaveVariable(){
        this.input = new Input();
        this.input.content = this.input.new Content();
        this.input.content.setId("");
    }
    @Data
    public  class Input{

        @JsonProperty("isCrush")
        private boolean isCrush;
        private String message;
        private String receiverId;
        private WaveRequestedFromEnum requestedFrom;
        private WaveRequestTypeEnum type;
        private Content content;

        @Data
        public class Content{
            private String id;
            private String header;
            private String text;
        }

        @JsonIgnore
        public Input setDefaultInputBody(){
            this.setCrush(false);
            this.setType(WaveRequestTypeEnum.hunch);
            this.setMessage("Hello");
            this.setReceiverId("rec_9921");
            this.setRequestedFrom(WaveRequestedFromEnum.profileVisitor);
            return  this;
        }
    }

    public InitiateWaveVariable setDefaultBody(){
        this.input= new Input().setDefaultInputBody();
        return  this;
    }

    public InitiateWaveVariable createRequest(boolean isCrush,String receiverId,String message,WaveRequestTypeEnum type, WaveRequestedFromEnum from,String receiverDp){

        this.input.setCrush(isCrush);
        this.input.setType(type);
        this.input.setMessage(message);
        this.input.setRequestedFrom(from);
        this.input.setReceiverId(receiverId);

        this.input.content.setHeader(type.getString());
        this.input.content.setText(receiverDp);
        return this;
    }

    @JsonIgnore
    public Input.Content getContentData(){
        return this.input.content;
    }

}
