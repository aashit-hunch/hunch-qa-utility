package org.hunch.core;


import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.hunch.constants.Config;
import org.hunch.log.LoggingOutputStream;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map.Entry;

public class HttpClient {
    protected static final Logger LOGGER = Logger.getLogger(HttpClient.class);
    private static boolean logRequest = Config.LOG_REQUEST_RESPONSE;
    private static boolean logResponse = Config.LOG_REQUEST_RESPONSE;

    public HttpClient() {
    }

    public static Response send(RequestSpecification request, RequestParams requestParams) {
        Response response = null;
        PrintStream ps = new PrintStream(new LoggingOutputStream(LOGGER,Level.INFO));
        if (logRequest) {
            request.filter(new RequestLoggingFilter(ps));
        }

        if (logResponse) {
            request.filter(new ResponseLoggingFilter(ps));
        }

        Iterator var4 = requestParams.getHm().entrySet().iterator();

        Entry multiPart;
        while(var4.hasNext()) {
            multiPart = (Entry)var4.next();
            request.header((String)multiPart.getKey(), multiPart.getValue(), new Object[0]);
        }

        var4 = requestParams.getQs().entrySet().iterator();

        while(var4.hasNext()) {
            multiPart = (Entry)var4.next();
            request.queryParam((String)multiPart.getKey(), new Object[]{multiPart.getValue()});
        }

        var4 = requestParams.getFormParam().entrySet().iterator();

        while(var4.hasNext()) {
            multiPart = (Entry)var4.next();
            request.formParam((String)multiPart.getKey(), new Object[]{multiPart.getValue()});
        }

        var4 = requestParams.getCookies().entrySet().iterator();

        while(var4.hasNext()) {
            multiPart = (Entry)var4.next();
            request.cookie((String)multiPart.getKey(), multiPart.getValue(), new Object[0]);
        }

        var4 = requestParams.getMultipartParam().entrySet().iterator();

        while(var4.hasNext()) {
            multiPart = (Entry)var4.next();
            request.multiPart((String)multiPart.getKey(), multiPart.getValue());
        }

        String bodyString = requestParams.getRequestBody();
        String nullString = "null";
        if (null != bodyString && bodyString.length() != 0) {
            bodyString = bodyString.replaceAll("\"SET_NULL\"", nullString);
            bodyString = bodyString.replaceAll("SET_NULL", nullString);
            requestParams.setRequestBody(bodyString);
            request.body(bodyString);
        }

        if(null==requestParams.getPath()){
            requestParams.setPath("");
        }

        try {
            Response var6;
            switch(requestParams.getMethodType()) {
                case HEAD:
                    var6 = (Response)request.head(requestParams.getUri()+requestParams.getPath().trim(), new Object[0]);
                    return var6;
                case GET:
                    var6 = (Response)request.get(requestParams.getUri()+requestParams.getPath().trim(), new Object[0]);
                    return var6;
                case PUT:
                    var6 = (Response)request.put(requestParams.getUri()+requestParams.getPath().trim(), new Object[0]);
                    return var6;
                case POST:
                    var6 = (Response)request.post(requestParams.getUri()+requestParams.getPath().trim(), new Object[0]);
                    return var6;
                case DELETE:
                    var6 = (Response)request.delete(requestParams.getUri()+requestParams.getPath().trim(), new Object[0]);
                    return var6;
                case PATCH:
                    var6 = (Response)request.patch(requestParams.getUri()+requestParams.getPath().trim(), new Object[0]);
                    return var6;
                case OPTIONS:
                    var6 = (Response)request.options(requestParams.getUri()+requestParams.getPath().trim(), new Object[0]);
                    return var6;
                default:
                    throw new RuntimeException("MethodType is not specified for the API method: " + requestParams.getUri());
            }
        } finally {
            if (ps != null) {
                ps.close();
            }

        }
    }

}
