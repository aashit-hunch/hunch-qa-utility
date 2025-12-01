package org.hunch.core;

public enum HttpMethodType {
    HEAD(1, "HEAD"),
    GET(2, "GET"),
    PUT(3, "PUT"),
    POST(4, "POST"),
    DELETE(5, "DELETE"),
    PATCH(6, "PATCH"),
    OPTIONS(7, "OPTIONS");

    private int code;
    private String name;

    private HttpMethodType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public HttpMethodType get(String name) {
        return valueOf(name);
    }
}
