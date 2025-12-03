package org.hunch.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hunch.utils.Common;

import java.util.ArrayList;
import java.util.List;

@Data
public class SetMultipleDps {
    public List<SetUserMultipleDpsInput2> setUserMultipleDpsInput2;
    @JsonProperty("isEditMode") public boolean isEditMode;


    @Data
    public class SetUserMultipleDpsInput2{
        public String dp;
        @JsonProperty("isPrimary") public boolean isPrimary;
    }


    public SetMultipleDps setDps(String dp, List<String> listOfDp){
        this.setUserMultipleDpsInput2 = new ArrayList<>();
        this.isEditMode =false;
        SetUserMultipleDpsInput2 dp1 = new SetUserMultipleDpsInput2();
        dp1.setDp(dp);
        dp1.setPrimary(true);
        this.setUserMultipleDpsInput2.add(dp1);
        for(String url : listOfDp){
            SetUserMultipleDpsInput2 otherDp = new SetUserMultipleDpsInput2();
            otherDp.setDp(url.replace("\\",""));
            otherDp.setPrimary(false);
            this.setUserMultipleDpsInput2.add(otherDp);
        }
        return this;
    }

    @Override
    public String toString(){
        return Common.mapper.writeValueAsString(this);
    }
}
