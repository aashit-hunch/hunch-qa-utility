package org.hunch.apis;

import com.github.javafaker.Faker;
import com.github.javafaker.File;
import io.restassured.response.Response;
import org.hunch.constants.Config;
import org.hunch.core.MimeType;
import org.hunch.enums.ActionTriggersForAcceptMatch;
import org.hunch.enums.WaveRequestTypeEnum;
import org.hunch.enums.WaveRequestedFromEnum;
import org.hunch.enums.core.RequestBodySchemaFileEnums;
import org.hunch.enums.core.SetupV2Journey;
import org.hunch.models.*;
import org.hunch.utils.Common;
import org.hunch.utils.GraphQLFileUtil;
import org.hunch.utils.ThreadUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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

    public static void setupV2WithSpecificData(SetupV2Journey journey){
        BaseApi apiObj = new BaseApi();
        apiObj.addHeader("Authorization", ThreadUtils.jwtToken.get());
        RequestBody requestBody= new RequestBody();
        requestBody.setQuery(GraphQLFileUtil.readGraphQLFromFileSystem(RequestBodySchemaFileEnums.SetupUserV2));
        SetupUserV2 setupUserV2 = new SetupUserV2();
        switch (journey) {
            case tags:
                setupUserV2.setTags();
            case ethnicity:
                setupUserV2.setEthinicity();
            case height:
                setupUserV2.setHeight();
            case relationshipType:
                setupUserV2.setDesiredRelationshipType();
            case datingPreferences:
                setupUserV2.setDatingPreference();
            case gender:
                setupUserV2.setGender();
            case dob:
                setupUserV2.setDob();
            case firstName:
                setupUserV2.setFirstName();
                break;
            default:
                return;
                // Do nothing
        }

        requestBody.setVariables(setupUserV2);
        apiObj.setRequestBody(requestBody.toString());
        apiObj.apiCall();
    }

    public static void setupV2FinalCall(){
        BaseApi apiObj = new BaseApi();
        apiObj.addHeader("Authorization", ThreadUtils.jwtToken.get());
        RequestBody requestBody= new RequestBody();
        requestBody.setQuery(GraphQLFileUtil.readGraphQLFromFileSystem(RequestBodySchemaFileEnums.SetupUserV2));
        SetupUserV2 setupUserV2 = new SetupUserV2();
        setupUserV2.setFinalData();
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
        //String jwtToken = FirebaseJWTManager.getInstance().transformTokenAToTokenB(resp.jsonPath().getString("data.verifyOtp"));
        //ThreadUtils.jwtToken.set(jwtToken);
    }

    public static Response uploadDps(){
        JSONArray obj = Common.getUserData();
        SetMultipleDps dps = new SetMultipleDps();
        for (int i=0;i<obj.length();i++){
            if(obj.getJSONObject(i).getString("gender").equalsIgnoreCase(ThreadUtils.userDto.get().getGender().getString())){
                String dp = obj.getJSONObject(i).getString("dp");
                List<String> arry = new ArrayList<>(obj.getJSONObject(i).getJSONArray("multiple_dps").toList().stream()
                        .map(Object::toString)
                        .map(url -> url.replace("\\", ""))
                        .toList());
                if(dp.contains("https://assets.hunch.in") || dp.contains("https://resources.hunch.in/")){
                    //Downloading and Uploading S3 Image
                    String mainDp = String.valueOf(UUID.randomUUID()+"_jpeg");
                    Common.downloadImage(dp,mainDp);
                    uploadProfileImage(mainDp);
                    dp= Config.S3_URL+mainDp;
                    for(int j=0;j<arry.size();j++){
                        String otherDp = String.valueOf(UUID.randomUUID()+"_jpeg");
                        Common.downloadImage(arry.get(j),otherDp);
                        uploadProfileImage(otherDp);
                        arry.set(j,Config.S3_URL+otherDp);
                    }
                }
                ThreadUtils.userDto.get().setMainDpUrl(dp);
                ThreadUtils.userDto.get().setOtherDpUrls(arry);
                break;
            }
        }
        dps.setDps(ThreadUtils.userDto.get().getMainDpUrl(),ThreadUtils.userDto.get().getOtherDpUrls());

        BaseApi apiObj = new BaseApi();
        apiObj.isURLEncoded(false);
        apiObj.addHeader("Authorization", ThreadUtils.jwtToken.get());
        RequestBody requestBody= new RequestBody();
        requestBody.setQuery(GraphQLFileUtil.readGraphQLFromFileSystem(RequestBodySchemaFileEnums.SET_MULTIPLE_DPS));
        requestBody.setVariables(dps);
        apiObj.setRequestBody(requestBody.toString());
        return apiObj.apiCall();
    }

    public static void sendBirdUpdateDp(String... dpUrl){
        SendBirdUpdate up = new SendBirdUpdate(ThreadUtils.userDto.get().getUser_id());
        SendBird obj = new SendBird();
        if (dpUrl.length>0){
            obj.setProfile_url(dpUrl[0]);
        }
        else  obj.setProfile_url(ThreadUtils.userDto.get().getMainDpUrl());
        up.setRequestBody(Common.mapper.writeValueAsString(obj));
        up.apiCall();
    }

    public static void sendBirdCreateUser(JSONObject userDetails){
        SendBirdCreate sendBirdCreate = new SendBirdCreate();
        SendBird obj = new SendBird();
        obj.setNickname(userDetails.getString("username"));
        obj.setUser_id(userDetails.getString("user_uid"));
        obj.setProfile_url(userDetails.getString("dp"));
        sendBirdCreate.setRequestBody(Common.mapper.writeValueAsString(obj));
        sendBirdCreate.apiCall();

    }

    public static Response sendBirdGetUser(String userUid){
        SendBirdGet sendBirdGet = new SendBirdGet(userUid);
        return sendBirdGet.apiCall();
    }

    public static Response sendBirdSendMessage(String channelUrl, String user_uid) {
        SendBirdSendMessage sendBirdSendMessage = new SendBirdSendMessage(channelUrl);
        SendBirdMessage obj = new SendBirdMessage();
        obj.setMessage(Faker.instance().superhero().descriptor());
        obj.setUser_id(user_uid);
        obj.setMessage_type("MESG");
        obj.setOrigin("crush");
        obj.setSend_push(true);
        sendBirdSendMessage.setRequestBody(Common.mapper.writeValueAsString(obj));
        return sendBirdSendMessage.apiCall();
    }

    public static void getUnifiedFeed(){
        BaseApi apiObj = new BaseApi();
        apiObj.addHeader("Authorization", ThreadUtils.jwtToken.get());
        RequestBody requestBody= new RequestBody();
        requestBody.setQuery(GraphQLFileUtil.readGraphQLFromFileSystem(RequestBodySchemaFileEnums.GET_UNIFIED_FEED));
        apiObj.setRequestBody(requestBody.toString());
        apiObj.apiCall();
    }

    public static Response initiateWave(String receiverUserUid,boolean isCrushWave,String receiverDp,String waveId,String... JWT){
        BaseApi apiObj = new BaseApi();
        if(JWT.length>0){
            apiObj.addHeader("Authorization", JWT[0]);
        }else apiObj.addHeader("Authorization", ThreadUtils.jwtToken.get());
        RequestBody requestBody= new RequestBody();
        requestBody.setQuery(GraphQLFileUtil.readGraphQLFromFileSystem(RequestBodySchemaFileEnums.INITIATE_WAVE));
        InitiateWaveVariable initiateWaveVariable = new InitiateWaveVariable();
        WaveRequestedFromEnum requestedFromEnum;
        String message;
        if(isCrushWave){
            requestedFromEnum= WaveRequestedFromEnum.notification;
            message = "Hey Crush !";
            if(null!=waveId&&!waveId.isEmpty()){
                initiateWaveVariable.getContentData().setId(waveId);
                requestedFromEnum = WaveRequestedFromEnum.waveSentTab;
            }
        }else {
            requestedFromEnum= WaveRequestedFromEnum.vibeTribe;
            message = "Waving Hi !";
        }
        initiateWaveVariable.createRequest(isCrushWave,receiverUserUid,message, WaveRequestTypeEnum.profilePhoto, requestedFromEnum,receiverDp);
        requestBody.setVariables(initiateWaveVariable);
        apiObj.setRequestBody(requestBody.toString());
        return apiObj.apiCall();
    }

    public static Response acceptWave(boolean isCrush,String waveRequestId,String JWT){
        BaseApi apiObj = new BaseApi();
        apiObj.addHeader("Authorization", JWT);
        RequestBody requestBody= new RequestBody();
        requestBody.setQuery(GraphQLFileUtil.readGraphQLFromFileSystem(RequestBodySchemaFileEnums.CONFIRM_MATCH_V2));
        ConfirmMatchV2 acceptWaveVariable = new ConfirmMatchV2();
        acceptWaveVariable.createRequest("Request Accepted :)",waveRequestId,isCrush, ActionTriggersForAcceptMatch.chat,WaveRequestedFromEnum.notification);
        requestBody.setVariables(acceptWaveVariable);
        requestBody.setOperationName("ConfirmMatchV2");
        apiObj.setRequestBody(requestBody.toString());
        return apiObj.apiCall();
    }

    public static Response updateGeoLocation(){
        BaseApi apiObj = new BaseApi();
        apiObj.addHeader("Authorization", ThreadUtils.jwtToken.get());
        UserGoelocation geo = new UserGoelocation();
        RequestBody requestBody= new RequestBody();
        requestBody.setQuery(GraphQLFileUtil.readGraphQLFromFileSystem(RequestBodySchemaFileEnums.USER_GEOLOCATION));
        requestBody.setVariables(geo);
        apiObj.setRequestBody(requestBody.toString());
        return apiObj.apiCall();
    }

    public static Response getPreSignedUrl(String fileName){
        BaseApi apiObj = new BaseApi();
        apiObj.addHeader("Authorization", ThreadUtils.jwtToken.get());
        RequestBody requestBody= new RequestBody();
        requestBody.setQuery(GraphQLFileUtil.readGraphQLFromFileSystem(RequestBodySchemaFileEnums.GET_PRE_SIGNED_URL));
        GetPreSignedUrl preSignedUrl = new GetPreSignedUrl("profile",fileName, MimeType.IMAGE_JPEG,"");
        requestBody.setVariables(preSignedUrl);
        apiObj.setRequestBody(requestBody.toString());
        return apiObj.apiCall();
    }

    public static void uploadImageToS3(MimeType mimeType,String presignedUrl,String filePath){
        UploadToS3 s3 = new UploadToS3(presignedUrl,mimeType);
        try{
            byte[] imageBytes = Files.readAllBytes(
                    Path.of(System.getProperty("user.dir")+"/src/main/resources/userImages/"+filePath+".jpeg")
            );
            s3.setRequestBody(imageBytes);
            s3.apiCall();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void uploadProfileImage(String fileName){
        String URL = getPreSignedUrl(fileName).jsonPath().getString("data.getPreSignedUrl");
        uploadImageToS3(MimeType.IMAGE_JPEG,URL,fileName);
    }
}
