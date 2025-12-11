package org.hunch.enums;

public enum WaveRequestedFromEnum {
    community("community"),
    vibeTribe("vibeTribe"),
    notification("notification"),
    publicProfile("publicProfile"),
    chat("chat"),
    waveSentTab("waveSentTab"),
    visitorsTab("visitorsTab"),
    waveListNudge("waveListNudge"),
    profileVisitor("profileVisitor"),
    spotlight("spotlight"),
    thisThatExpandedView("thisThatExpandedView");

    private final String value;

    WaveRequestedFromEnum(String value) {
        this.value = value;
    }

    public String getString() {
        return value;
    }

    // Optional: Get enum from string value
    public static WaveRequestedFromEnum fromString(String value) {
        for (WaveRequestedFromEnum type : WaveRequestedFromEnum.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown value for WaveRequestedFrom : " + value);
    }
}
