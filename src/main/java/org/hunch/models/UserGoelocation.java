package org.hunch.models;

import lombok.Data;
import org.hunch.constants.GlobalData;

@Data
public class UserGoelocation {

    private Input input;

    @Data
    public class Input {
        private Double latitude;
        private Double longitude;
    }

    public UserGoelocation(){
        this.input = new Input();
        this.input.setLatitude(GlobalData.LAT_LONG.getLatitude());
        this.input.setLongitude(GlobalData.LAT_LONG.getLongitude());
    }

    @Override
    public String toString() {
        return org.hunch.utils.Common.mapper.writeValueAsString(this);
    }
}
