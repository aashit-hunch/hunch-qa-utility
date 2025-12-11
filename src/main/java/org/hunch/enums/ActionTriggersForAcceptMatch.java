package org.hunch.enums;

public enum ActionTriggersForAcceptMatch {


    chat("chat"),
    full_profile_view("full_profile_view"),
    card_list_view("card_list_view");

    private final String value;

    ActionTriggersForAcceptMatch(String value) {
        this.value = value;
    }

    public String getString() {
        return value;
    }

    public static ActionTriggersForAcceptMatch fromString(String value) {
        for (ActionTriggersForAcceptMatch at : ActionTriggersForAcceptMatch.values()) {
            if (at.value.equalsIgnoreCase(value)) {
                return at;
            }
        }
        throw new IllegalArgumentException("Unknown value for Gender: " + value);
    }
}

