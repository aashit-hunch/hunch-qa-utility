package org.hunch.constants;

import org.hunch.enums.Gender;
import org.hunch.enums.core.LatLong;
import org.hunch.enums.core.SetupV2Journey;
import org.hunch.enums.core.UserOperations;

public final class GlobalData {

    public static final String USER_OPERATION = String.valueOf(System.getProperty("operation", "gen.user"));
    public static final UserOperations USER_OPERATION_TYPE = UserOperations.fromString(USER_OPERATION);
    public static final int THREAD_COUNT = Integer.parseInt(System.getProperty("user.count", "1"));
    public static final String PHONE_NUMBER = String.valueOf(System.getProperty("phone.number"));
    public static final String SETUP_USER_V2 = System.getProperty("user.onboarding", "images");
    public static final SetupV2Journey JOURNEY = SetupV2Journey.fromString(SETUP_USER_V2);
    public static final Gender GENDER_TYPE = Gender.fromString(System.getProperty("user.gender", "male"));
    public static final Gender GENDER_TYPE_PREFERENCE = Gender.fromString(System.getProperty("user.preference", "female"));
    public static final boolean USER_PROVIDED_DATA = Boolean.parseBoolean(System.getProperty("user.custom", "false"));
    public static final LatLong LAT_LONG = LatLong.valueOf(System.getProperty("user.location", "USA_NY"));
    public static final boolean ENABLE_INFO_LOGS = Boolean.parseBoolean(System.getProperty("enable.info.logs", "false"));

    /*static {
        System.out.println("[DEBUG] user.custom property: '" + System.getProperty("user.custom") + "'");
        System.out.println("[DEBUG] USER_PROVIDED_DATA: " + USER_PROVIDED_DATA);
        System.out.println("[DEBUG] PHONE_NUMBER property: " + PHONE_NUMBER);
        System.out.println("[DEBUG] SETUP_USER_V2 property: " + SETUP_USER_V2);
        System.out.println("[DEBUG] JOURNEY: " + JOURNEY);
        System.out.println("[DEBUG] ENABLE_INFO_LOGS: " + ENABLE_INFO_LOGS);
    }*/




}
