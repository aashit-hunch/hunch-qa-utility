package org.hunch.enums;

public enum WaveRequestSorting {
    all("all"),
    matchScore("match_score"),
    activeToday("active_today"),
    targetMatchDistance("target_match_distance");

    private final String value;

    WaveRequestSorting(String value) {
        this.value = value;
    }

    public String getString() {
        return value;
    }

    public static WaveRequestSorting fromString(String value) {
        for (WaveRequestSorting type : WaveRequestSorting.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown value for WaveRequestSorting: " + value);
    }
}