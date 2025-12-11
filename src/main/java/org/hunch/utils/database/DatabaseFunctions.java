package org.hunch.utils.database;

import org.apache.log4j.Logger;
import org.hunch.constants.GlobalData;
import org.hunch.models.db.InsertUserFeatureUsage;
import org.hunch.models.db.InsertUserVerificationTrail;
import org.hunch.models.db.InsertUsers;
import org.hunch.models.db.UserEntitlements;
import org.hunch.operations.GenerateUserOperations;
import org.hunch.utils.Common;
import org.hunch.utils.ThreadUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Locale;
import static org.hunch.operations.GenerateUserOperations.dbOps;

public class DatabaseFunctions {
    private static final Logger LOGGER = Logger.getLogger(DatabaseFunctions.class);

    public static void generateRandomNewUser() {
        try {
            GenerateUserOperations genUser = new GenerateUserOperations();
            String userUid = genUser.generateRandomUUID();
            String phoneNumber ;
            if(null!= GlobalData.PHONE_NUMBER && !GlobalData.PHONE_NUMBER.equalsIgnoreCase("null") && !GlobalData.PHONE_NUMBER.isEmpty() && GlobalData.USER_PROVIDED_DATA){
                phoneNumber = GlobalData.PHONE_NUMBER;
            }else {
                phoneNumber=genUser.generateRandomNumber();
            }
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
            return Common.generateFirebaseToken(arr.getJSONObject(0));
        }
        catch (Exception e){
            throw new RuntimeException("Exception occurred while fetching JWT from Phone Number : "+e.getMessage());
        }
    }

    public static void increaseCrushLimit(String userUid, int incrementBy){
        try{
            String query = "select * from user_feature_usage ufu where ufu.user_uid ='"+userUid+"' and ufu.feature_type = 'crush';";

            JSONArray result =dbOps.executeQuery(query);
            if(result.length()>0){
                int currentLimit = result.getJSONObject(0).getJSONObject("credits").getInt("free");
                int newLimit = currentLimit + incrementBy;
                result.getJSONObject(0).getJSONObject("credits").put("free",newLimit);
                int updateStatus =dbOps.update("user_feature_usage",new JSONObject().put("credits",result.getJSONObject(0).getJSONObject("credits")),result.getJSONObject(0).getInt("id"),"id");
                LOGGER.info("Crush Limit Increase Status : "+updateStatus);
            }
            else {
                InsertUserFeatureUsage ins = new InsertUserFeatureUsage(userUid,"crush",1);
                ins.initializeCredits(10,0,0,0);
                int id = dbOps.insert("user_feature_usage",ins.toJsonObject(),"id");
                LOGGER.info("Crush Limit Insert Status : "+id);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Exception occurred while increasing Crush Limit : "+e.getMessage());
        }
    }

    public static void increaseCrushLimitByPhone(String phone_number, int incrementBy) {
        JSONArray result = getUserDataByPhone(phone_number);
        if (result.length() > 0) {
            String userUid = result.getJSONObject(0).getString("user_uid");
            increaseCrushLimit(userUid, incrementBy);
        }
        else {
            throw new RuntimeException("No user found with phone number: " + phone_number);
        }
    }

    public static JSONArray getUserDataByPhone(String phone_number){
        String query = "select * from users where phone_number = '" + phone_number + "';";
        return dbOps.executeQuery(query);
    }

    public static JSONArray getGenderPreferredData(){
        return getGenderPreferredData(ThreadUtils.userDto.get().getUser_id(),ThreadUtils.userDto.get().getGender().getString(),ThreadUtils.userDto.get().getDating_preferences().get(0).getString());
    }

    public static JSONArray getGenderPreferredData(String user_uid,String gender, String preference) {
        String query = "SELECT \n" +
                "    u.user_uid,\n" +
                "    u.phone_number,\n" +
                "    u.gender,\n" +
                "    u.dating_preference,\n" +
                "    u.ethnicity,\n" +
                "    u.email,\n" +
                "    u.first_name,\n" +
                "    u.username,\n" +
                "    u.dp\n" +
                "FROM \n" +
                "    users u\n" +
                "WHERE \n" +
                "    u.dating_preference @> ARRAY['"+gender+"']\n" +
                "    AND u.gender = '"+preference+"'\n" +
                "    AND u.is_profile_setup_status = 'completed'\n" +
                "    AND u.verification_status = 'verified'\n" +
                "    AND u.status = 'active'\n" +
                "    AND u.source = 'custom_phone_number'\n" +
                "    AND u.is_deleted = false\n" +
                "    AND u.user_uid <> '"+user_uid+"'\n" +
                "    AND NOT EXISTS (\n" +
                "        SELECT 1\n" +
                "        FROM goss_requests g\n" +
                "        WHERE \n" +
                "            (g.sender_id = u.user_uid AND g.receiver_id = '"+user_uid+"')\n" +
                "            OR\n" +
                "            (g.sender_id = '"+user_uid+"' AND g.receiver_id = u.user_uid)\n" +
                "    )\n" +
                "LIMIT 5000;";
        return Common.shuffleJsonArray(dbOps.executeQuery(query));
    }

    public static int deleteWaveCrush(String senderId, String receiverId){

        return dbOps.deleteWhere(
                "goss_requests",
                "sender_id IN (?, ?) AND receiver_id IN (?, ?)",
                senderId, receiverId, senderId, receiverId
        );
    }

    public static int makeUserPremium(String userUid){

        JSONArray entitlements = dbOps.executeQuery(
            "SELECT * FROM user_entitlements ue WHERE ue.user_uid = ? AND ue.expire_date > now() LIMIT 10",
            userUid
        );
        if(entitlements.length()>0){
            LOGGER.info("User is already Premium. No need to insert new entitlement.");
            return entitlements.getJSONObject(0).getInt("auto_id");
        }
        else {
            dbOps.deleteWhere(
                    "user_entitlements",
                    "user_uid IN ( ?)",userUid);

            UserEntitlements ue = new UserEntitlements(
                    userUid,
                    "hunch_offer_v5_welcome_20",
                    "offer_weekly",
                    "offer_weekly",
                    "NORMAL",
                    7
            );
            return dbOps.insert("user_entitlements",ue.toJsonObject(),"auto_id");
        }
    }



}
