package org.hunch.enums.core;


public enum SetupV2Journey {
    firstName("firstName"),
    dob("dob"),
    gender("gender"),
    datingPreferences("datingPreferences"),
    relationshipType("relationshipType"),
    height("height"),
    ethnicity("ethnicity"),
    tags("tags"),
    mbti("mbti"),
    images("images"),;

    private final String value;

    SetupV2Journey(String value) {
        this.value = value;
    }

    public String getString() {
        return value;
    }

    public static SetupV2Journey fromString(String value) {
        for (SetupV2Journey journey : SetupV2Journey.values()) {
            if (journey.value.equalsIgnoreCase(value)) {
                return journey;
            }
        }
        throw new IllegalArgumentException("Unknown value for journey: " + value);
    }
}

