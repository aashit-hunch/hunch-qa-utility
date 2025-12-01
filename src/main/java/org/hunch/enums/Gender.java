package org.hunch.enums;

public enum Gender {
    male("male"),
    female("female"),
    nonBinary("non_binary");

    private final String value;

    Gender(String value) {
        this.value = value;
    }

    public String getString() {
        return value;
    }

    public static Gender fromString(String value) {
        for (Gender gender : Gender.values()) {
            if (gender.value.equalsIgnoreCase(value)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Unknown value for Gender: " + value);
    }
}