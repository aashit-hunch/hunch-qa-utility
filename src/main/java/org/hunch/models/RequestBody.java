package org.hunch.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.apache.log4j.Logger;
import org.hunch.utils.Common;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestBody {
    private static final Logger LOGGER = Logger.getLogger(RequestBody.class);

    private String query;
    private Object variables;
    private String operationName;



/*    public RequestBody(String query){
        this.query = query;
    }
    public RequestBody(String query, Object variable){
        this.query = query;
        this.variables = variable;
    }*/

    private static String jsonRequestBody(RequestBody req) {
        try {
            return Common.mapper.writeValueAsString(req);
        } catch (Exception e) {
            LOGGER.error("Exception occurred :" + e.getMessage());
        }
        return null;
    }

    @Override
    public String toString(){
        try {
            return Common.mapper.writeValueAsString(this);
        } catch (Exception e) {
            LOGGER.error("Exception occurred :" + e.getMessage());
        }
        return null;
    }
}