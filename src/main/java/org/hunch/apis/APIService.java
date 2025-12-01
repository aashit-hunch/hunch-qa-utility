package org.hunch.apis;

import io.restassured.response.Response;
import org.hunch.enums.core.RequestBodySchemaFileEnums;
import org.hunch.models.MBTIPolls;
import org.hunch.models.RequestBody;
import org.hunch.models.SetupUserV2;
import org.hunch.models.SmsLoginOtp;
import org.hunch.utils.FirebaseJWTManager;
import org.hunch.utils.GraphQLFileUtil;
import org.hunch.utils.ThreadUtils;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class APIService {

    public static void setupV2WithRandomData(){
        BaseApi apiObj = new BaseApi();
        apiObj.addHeader("Authorization", ThreadUtils.jwtToken.get());
        RequestBody requestBody= new RequestBody();
        requestBody.setQuery(GraphQLFileUtil.readGraphQLFromFileSystem(RequestBodySchemaFileEnums.SetupUserV2));
        SetupUserV2 setupUserV2 = new SetupUserV2();
        setupUserV2.setRandomData();
        requestBody.setVariables(setupUserV2);
        apiObj.setRequestBody(requestBody.toString());
        apiObj.apiCall();
    }

    public static void setRandomMbti(){
        BaseApi apiObj = new BaseApi();
        apiObj.addHeader("Authorization", ThreadUtils.jwtToken.get());
        RequestBody requestBody= new RequestBody();
        requestBody.setQuery(GraphQLFileUtil.readGraphQLFromFileSystem(RequestBodySchemaFileEnums.MBTI));
        apiObj.setRequestBody(requestBody.toString());
        Response resp =apiObj.apiCall();
        for (int i=0;i<resp.jsonPath().getList("data.getMbtiPolls.items").size();i++){
            List<Map<String,Object>> mbtiPolls = resp.jsonPath().getList("data.getMbtiPolls.items["+i+"].sections[0].options");
            Collections.shuffle(mbtiPolls);
            BaseApi mbtiPollsApisObj = new BaseApi();
            mbtiPollsApisObj.addHeader("Authorization", ThreadUtils.jwtToken.get());
            MBTIPolls mbti = new MBTIPolls();
            mbti.setUpData(resp.jsonPath().getString("data.getMbtiPolls.items["+i+"].sections[0].id")
                    ,String.valueOf(mbtiPolls.get(0).get("id"))
                    ,""
                    ,i);
            RequestBody mbtiPollsObj = new RequestBody();
            mbtiPollsObj.setQuery(GraphQLFileUtil.readGraphQLFromFileSystem(RequestBodySchemaFileEnums.MBTIPolls));
            mbtiPollsObj.setVariables(mbti);
            mbtiPollsApisObj.setRequestBody(mbtiPollsObj.toString());
            mbtiPollsApisObj.apiCall();
        }
    }

    public static void sendOtp(String phoneNumber){
        BaseApi apiObj = new BaseApi();
        RequestBody requestBody= new RequestBody();
        requestBody.setQuery(GraphQLFileUtil.readGraphQLFromFileSystem(RequestBodySchemaFileEnums.SMS_LOGIN_OTP));
        SmsLoginOtp requestBodyObj = new SmsLoginOtp();
        requestBodyObj.setPhoneNumber(phoneNumber);
        requestBody.setVariables(requestBodyObj);
        apiObj.setRequestBody(requestBody.toString());
        apiObj.apiCall();
    }

    public static void verifyOtp(String phoneNumber){
        BaseApi apiObj = new BaseApi();
        RequestBody requestBody= new RequestBody();
        requestBody.setQuery(GraphQLFileUtil.readGraphQLFromFileSystem(RequestBodySchemaFileEnums.VERIFY_OTP));
        SmsLoginOtp requestBodyObj = new SmsLoginOtp();
        requestBodyObj.setPhoneNumber(phoneNumber);
        requestBodyObj.setOtp("123456");
        requestBody.setVariables(requestBodyObj);
        apiObj.setRequestBody(requestBody.toString());
        Response resp = apiObj.apiCall();
        String jwtToken = FirebaseJWTManager.getInstance().transformTokenAToTokenB(resp.jsonPath().getString("data.verifyOtp"));
        ThreadUtils.jwtToken.set(jwtToken);
    }
}
