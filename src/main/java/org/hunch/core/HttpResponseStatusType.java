package org.hunch.core;

public enum HttpResponseStatusType {
    OK_200(200, "OK"),
    CREATED_201(201, "Created"),
    ACCEPTED_202(202, "Accepted"),
    NO_CONTENT_204(204, "No Content"),
    NOT_MODIFIED_304(304, "Not Modified"),
    BAD_REQUEST_400(400, "Bad Request"),
    UNAUTHORIZED_401(401, "Unauthorized"),
    FORBIDDEN_403(403, "Forbidden"),
    NOT_FOUND_404(404, "Not Found"),
    CONFLICT_409(409, "Conflict"),
    PRECONDITION_FAILED(412, "Precondition Failed"),
    UNSUPPORTED_MEDIA_TYPE_415(415, "Unsupported Media Type"),
    EXPECTATION_FAILED_417(417, "Expectation Failed"),
    UNPROCESSABLE_ENTITY_422(422, "Unprocessable Entity"),
    INTERNAL_ERROR_500(500, "Internal Server Error"),
    FAILED_DEPENDENCY_424(424, "Failed Dependency"),
    RESET_CONTENT_205(205, "Reset Content"),
    TEAPOT_418(418, "I'm a teapot"),
    GONE_410(410, "Gone"),
    NOT_IMPLEMENTED_501(501, "Not Implemented");

    private int code;
    private String message;

    private HttpResponseStatusType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
