package org.hunch.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hunch.enums.ActionTriggersForAcceptMatch;
import org.hunch.enums.WaveRequestedFromEnum;

@Data
public class ConfirmMatchV2 {
    public Input input;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Input{
        public ActionTriggersForAcceptMatch actionTrigger;
        public String channelUrl;
        public Boolean createChannel;
        public String id;
        public String matchType;
        public String message;
        public WaveRequestedFromEnum requestedFrom;

        public Input setDefaultInputBody(){
            this.actionTrigger=ActionTriggersForAcceptMatch.chat;
            this.channelUrl=null;
            this.createChannel=true;
            this.id="match_1234";
            this.matchType="hunch";
            this.message="Hi there!";
            this.requestedFrom=WaveRequestedFromEnum.vibeTribe;
            return  this;
        }
    }

    public ConfirmMatchV2 setDefaultBody(){
        this.input= new Input().setDefaultInputBody();
        return  this;
    }

    public ConfirmMatchV2 createRequest(String message,String id,boolean isCrush,ActionTriggersForAcceptMatch actionTrigger,WaveRequestedFromEnum requested){
        Input obj = new Input();
        obj.setId(id);
        obj.setMessage(message);
        obj.setCreateChannel(true);
        if(isCrush){
            obj.setMatchType("crush");
        }
        else
        {
            obj.setMatchType("wave");
        }
        obj.setActionTrigger(actionTrigger);
        obj.setRequestedFrom(requested);
        this.input=obj;
        return this;
    }


}
