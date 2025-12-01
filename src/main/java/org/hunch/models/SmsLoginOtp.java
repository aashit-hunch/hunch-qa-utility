package org.hunch.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SmsLoginOtp {
    private String phoneNumber;
    private String otp;
}
