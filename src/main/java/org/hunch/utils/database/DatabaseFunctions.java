package org.hunch.utils.database;

import org.apache.log4j.Logger;
import org.hunch.models.db.InsertUserVerificationTrail;
import org.hunch.models.db.InsertUsers;
import org.hunch.operations.GenerateUserOperations;
import org.hunch.utils.Common;
import org.hunch.utils.FirebaseJWTManager;
import org.hunch.utils.ThreadUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;
import static org.hunch.operations.GenerateUserOperations.dbOps;

public class DatabaseFunctions {
    private static final Logger LOGGER = Logger.getLogger(DatabaseFunctions.class);

    public static void generateRandomNewUser(){
        try {
            GenerateUserOperations genUser = new GenerateUserOperations();
            String userUid = genUser.generateRandomUUID();
            String phoneNumber = genUser.generateRandomNumber();
            String email = userUid+"@hunchmobile.com";
            InsertUsers ins = new InsertUsers(userUid, genUser.generateRandomUserName(), phoneNumber, email, "android", "2.56.3", genUser.generateRandomReferralCode().toUpperCase(Locale.ROOT), genUser.generateRandomAdId());
            ins.insertSignupLocationDetails("New Delhi","National Capital Territory of Delhi","India");
            int insertStatus = dbOps.insert("users", ins.toJsonObject(),"auto_id");
            LOGGER.info("Insert Status: "+insertStatus);
            LOGGER.info("Generated User Details : \n User UID: "+userUid+"\n Phone Number: "+phoneNumber+"\n Email: "+email);
            ThreadUtils.userDto.get().setEmail(email);
            ThreadUtils.userDto.get().setUser_id(userUid);
            ThreadUtils.userDto.get().setPhone_number(phoneNumber);

            ThreadUtils.jwtToken.set(fetchJwtFromPhoneNumber(phoneNumber));
        }
        catch (Exception e){
            throw new RuntimeException("Exception occurred while generating random user : "+e.getMessage());
        }
    }

    public static void livenessDataSet(){
        InsertUserVerificationTrail verify = new InsertUserVerificationTrail(ThreadUtils.userDto.get().getUser_id()
                ,"onboard"
                ,ThreadUtils.userDto.get().getMainDpUrl()
                ,"verified");
        JSONObject live = Common.getLivenessData();
        verify.setAnalysisFromJson(live.getJSONObject("analysis"));
        verify.setLivenessFromJson(live.getJSONObject("liveness"));
        verify.setFaceMatchFromJson(live.getJSONObject("face_match"));
        verify.setLivenessFromJson(live.getJSONObject("liveness_analysis"));
        int insertStatus = dbOps.insert("user_verification_trail", verify.toJsonObject(),"auto_id");
        LOGGER.info("Liveness Data Insert Status: "+insertStatus);

        int userStatusUpdate =dbOps.update("users",new JSONObject().put("verification_status","verified"),ThreadUtils.userDto.get().getUser_id(),"user_uid");
        LOGGER.info("User Verification Status Update: "+userStatusUpdate);
    }

    public static void updateUserImages(){
        JSONObject values = new JSONObject();
        values.put("dp",ThreadUtils.userDto.get().getMainDpUrl());
        values.put("multiple_dps",new JSONArray(ThreadUtils.userDto.get().getOtherDpUrls()));
        int updateStatus = dbOps.update("users",values,ThreadUtils.userDto.get().getUser_id(),"user_uid");
        LOGGER.info("User Images Update Status: "+updateStatus);
    }

    public static String fetchJwtFromPhoneNumber(String phoneNumber){
        try{
            String query = "select *  from users where phone_number = '" + phoneNumber + "';";
            JSONArray arr =dbOps.executeQuery(query);
            if (arr.isEmpty()){
                LOGGER.info("No User Found with Phone Number : "+phoneNumber);
                return null;
            }
            FirebaseJWTManager.UserDetails ud = new FirebaseJWTManager.UserDetails.Builder().setUserId(arr.getJSONObject(0).getString("user_uid"))
                    .setEmail(arr.getJSONObject(0).getString("email"))
                    .setPhoneNumber(arr.getJSONObject(0).getString("phone_number"))
                    .build();

            FirebaseJWTManager.TokenPair tk =FirebaseJWTManager.getInstance().performTokenExchange(ud);
            return tk.getTokenB();
        }
        catch (Exception e){
            throw new RuntimeException("Exception occurred while fetching JWT from Phone Number : "+e.getMessage());
        }
    }
}
