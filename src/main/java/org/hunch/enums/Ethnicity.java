package org.hunch.enums;

public enum Ethnicity {

    ASIAN("Asian"),
    BLACK_AFRICAN("Black/African"),
    BLACK_AFRICAN_AMERICAN("Black / African American"),
    HISPANIC_LATINO("Hispanic/Latino"),
    MIDDLE_EASTERN("Middle Eastern"),
    NATIVE_AMERICAN_INDIGENOUS("Native American / Indigenous"),
    OTHER("Other"),
    PACIFIC_ISLANDER("Pacific Islander"),
    SOUTH_ASIAN("South Asian"),
    WHITE_CAUCASIAN("White/Caucasian");

    private final String displayName;

    Ethnicity(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Ethnicity fromString(String displayName) {
        for (Ethnicity gender : Ethnicity.values()) {
            if (gender.displayName.equalsIgnoreCase(displayName)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Unknown value for Ethnicity: " + displayName);
    }
}