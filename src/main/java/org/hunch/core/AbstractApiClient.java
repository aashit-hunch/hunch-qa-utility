package org.hunch.core;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hunch.utils.Common;

import java.util.Map;

public abstract class AbstractApiClient  {

    private RequestSpecification req;
    private  RequestParams reqParam = new RequestParams();

    public AbstractApiClient(String baseUri,HttpMethodType method){
        this.req = RestAssured.given();
        this.reqParam.setUri(baseUri);
        this.reqParam.setMethodType(method);
    }

    public void addHeader(String key, String value){
        this.reqParam.addHeader(key,value);
    }
    public void removeHeader(String key){
        this.reqParam.removeHeader(key);
    }
    public  void addHeaders(Map<String,String> headersMap){
        this.reqParam.setHm(headersMap);
    }

    public void addQueryParam(String key, String value){
        this.reqParam.addQueryParam(key, value);
    }

    public void setRequestBody(String body) {
        this.reqParam.setRequestBody(body);
    }
    public void setRequestBody(Object body) {
        this.reqParam.setRequestBody(body);
    }

    public void isURLEncoded(boolean isEncoded){
        this.req.urlEncodingEnabled(isEncoded);
    }

    public void addReqPath(String path){
        this.reqParam.setPath(path);
    }

    public Response apiCall(){
        RequestParams newParam =  this.reqParam;
        RequestSpecification request = this.req;
        this.reqParam.setResponse(HttpClient.send(request, newParam));
        Common.validateApiStatusCode(reqParam);
        return this.reqParam.getResponse();
    }
}
