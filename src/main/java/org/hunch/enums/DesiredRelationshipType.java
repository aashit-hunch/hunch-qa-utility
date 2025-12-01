package org.hunch.enums;

public enum DesiredRelationshipType {
    shortTermButOpenToLong("shortTermButOpenToLong"),
    longTermButOpenToShort("longTermButOpenToShort"),
    shortTermOnly("shortTermOnly"),
    longTermOnly("longTermOnly"),
    newFriends("newFriends"),
    stillFiguringItOut("stillFiguringItOut"),
    longTermRelationship("longTermRelationship"),
    funCasualDates("funCasualDates"),
    intimacyWithoutCommitment("intimacyWithoutCommitment"),
    findingFriends("findingFriends");

    private final String value;

    DesiredRelationshipType(String value) {
        this.value = value;
    }

    public String getString() {
        return value;
    }

    public static DesiredRelationshipType fromString(String value) {
        for (DesiredRelationshipType type : DesiredRelationshipType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown value for DesiredRelationshipType: " + value);
    }
}