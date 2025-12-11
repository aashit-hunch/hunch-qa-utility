package org.hunch.enums;

public enum WaveRequestTypeEnum {
    blank("blank"),
    text("text"),
    poll("poll"),
    comment("comment"),
    hunch("hunch"),
    prompt("prompt"),
    profile("profile"),
    vibeTribePoll("vibeTribePoll"),
    vibeTribePrompt("vibeTribePrompt"),
    thisOrThat("thisOrThat"),
    profilePhoto("profilePhoto"),
    aiMatchExplainer("aiMatchExplainer"),
    scoreCard("scoreCard");

    private final String value;

    WaveRequestTypeEnum(String value) {
        this.value = value;
    }

    public String getString() {
        return value;
    }

    public static WaveRequestTypeEnum fromString(String value) {
        for (WaveRequestTypeEnum type : WaveRequestTypeEnum.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown value for WaveRequestType : " + value);
    }
}