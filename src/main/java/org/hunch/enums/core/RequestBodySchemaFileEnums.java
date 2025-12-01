package org.hunch.enums.core;

public enum RequestBodySchemaFileEnums {

    SetupUserV2 ("SetupUserV2.graphql"),
    MBTI("MBTI.graphql"),
    MBTIPolls("MBTIPolls.graphql"),
    SMS_LOGIN_OTP("SmsLoginOtp.graphql"),
    VERIFY_OTP("VerifyOtp.graphql");

    String value;
    RequestBodySchemaFileEnums(String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }
}
