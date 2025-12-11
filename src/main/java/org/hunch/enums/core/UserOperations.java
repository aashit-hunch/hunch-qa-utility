package org.hunch.enums.core;

public enum UserOperations {

    GENERATE_USER("gen.user"),
    GENERATE_WAVE_SENT("gen.wave.sent"),
    GENERATE_CRUSH_SENT("gen.crush.sent"),
    GENERATE_WAVE_RECEIVED("gen.wave.received"),
    GENERATE_CRUSH_RECEIVED("gen.crush.received"),
    GENERATE_CRUSH_SENT_ACCEPTED("gen.crush.sent.accepted"),
    GENERATE_CRUSH_RECEIVED_ACCEPTED("gen.crush.received.accepted");

    private final String value;

    UserOperations(String value) {
        this.value = value;
    }

    public String getString() {
        return value;
    }

    public static UserOperations fromString(String value) {
        for (UserOperations operation : UserOperations.values()) {
            if (operation.value.equalsIgnoreCase(value)) {
                return operation;
            }
        }
        throw new IllegalArgumentException("Unknown value for operation: " + value);
    }
}
