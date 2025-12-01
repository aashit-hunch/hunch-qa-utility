package org.hunch.models;

import lombok.Data;

@Data
public class MBTIPolls {
    public Input input;
    public class Input{
        public String pollId;
        public String optionId;
        public String impression;
        public int voteCount;
    }

    public MBTIPolls setUpData(String pollId, String optionId, String impression,int voteCount){
        this.input = new Input();
        this.input.pollId = pollId;
        this.input.optionId = optionId;
        this.input.impression = impression;
        this.input.voteCount = voteCount;
        return this;
    }
}
