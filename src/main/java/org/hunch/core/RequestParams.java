package org.hunch.core;

import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class RequestParams {
    private String uri;
    private Object requestBody;
    private Response response;
    private HttpMethodType methodType;
    private MimeType mimeType;
    private Map<String, String> hm = new HashMap();
    private Map<String, String> cookies = new HashMap();
    private Map<String, String> qs = new HashMap();
    private Map<String, String> formParam = new HashMap();
    private Map<String, Object> multipartParam = new HashMap();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String path;

    public RequestParams() {
    }

    public RequestParams(String uri, HttpMethodType methodType, MimeType mimeType) {
        this.uri = uri;
        this.methodType = methodType;
        this.mimeType = mimeType;
    }

    public Map<String, Object> getMultipartParam() {
        return this.multipartParam;
    }

    public void setMultipartParam(Map<String, Object> multipartParam) {
        this.multipartParam = multipartParam;
    }

    public void setMultiPartParam(String key, Object value) {
        this.multipartParam.put(key, value);
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Object getRequestBody() {
        return this.requestBody;
    }

    public void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }

    public Response getResponse() {
        return this.response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public HttpMethodType getMethodType() {
        return this.methodType;
    }

    public void setMethodType(HttpMethodType methodType) {
        this.methodType = methodType;
    }

    public MimeType getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    public Map<String, String> getHm() {
        return this.hm;
    }

    public void setHm(Map<String, String> hm) {
        this.hm = hm;
    }

    public Map<String, String> getQs() {
        return this.qs;
    }

    public void setQs(Map<String, String> qs) {
        this.qs = qs;
    }

    public void addHeader(String key, String value) {
        this.hm.put(key, value);
    }

    public void removeHeader(String key) {
        this.hm.remove(key);
    }

    public void addQueryParam(String key, String value) {
        this.qs.put(key, value);
    }

    public Map<String, String> getFormParam() {
        return this.formParam;
    }

    public void setFormParam(Map<String, String> formParam) {
        this.formParam = formParam;
    }

    public void addFormParam(String key, String value) {
        this.formParam.put(key, value);
    }

    public Map<String, String> getCookies() {
        return this.cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public void addCookie(String key, String value) {
        this.cookies.put(key, value);
    }

    public String toString() {
        return "RequestParams{uri='" + this.uri + '\'' + ", requestBody='" + this.requestBody + '\'' + ", response=" + this.response + ", methodType=" + this.methodType + ", mimeType=" + this.mimeType + ", hm=" + this.hm + ", cookies=" + this.cookies + ", qs=" + this.qs + ", formParam=" + this.formParam + ", multipartParam=" + this.multipartParam + '}';
    }
}
