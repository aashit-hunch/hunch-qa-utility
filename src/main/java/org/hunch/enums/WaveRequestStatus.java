package org.hunch.enums;

public enum WaveRequestStatus {
    pending("pending"),
    accepted("accepted"),
    rejected("rejected"),
    unmatched("unmatched"),
    deleted("deleted"),
    notSet("not_set"),
    dismissed("dismissed");

    private final String value;

    WaveRequestStatus(String value) {
        this.value = value;
    }

    public String getString() {
        return value;
    }

    public static WaveRequestStatus fromString(String value) {
        for (WaveRequestStatus type : WaveRequestStatus.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown value for WaveRequestStatus: " + value);
    }
}