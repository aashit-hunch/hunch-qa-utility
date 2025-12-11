package org.hunch.enums.core;

public enum RequestBodySchemaFileEnums {

    SetupUserV2 ("SetupUserV2.graphql"),
    MBTI("MBTI.graphql"),
    MBTIPolls("MBTIPolls.graphql"),
    SMS_LOGIN_OTP("SmsLoginOtp.graphql"),
    VERIFY_OTP("VerifyOtp.graphql"),
    SET_MULTIPLE_DPS("SetMultipleDps.graphql"),
    GET_UNIFIED_FEED("GetUnifiedFeed.graphql"),
    INITIATE_WAVE("InitiateWave.graphql"),
    CONFIRM_MATCH_V2("ConfirmMatchV2.graphql"),
    USER_GEOLOCATION("UserGoelocation.graphql");

    String value;
    RequestBodySchemaFileEnums(String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }
}
