package org.hunch.utils.database;

import org.apache.log4j.Logger;
import org.hunch.models.db.InsertUsers;
import org.hunch.operations.GenerateUserOperations;
import org.hunch.utils.FirebaseJWTManager;
import org.hunch.utils.ThreadUtils;
import org.json.JSONArray;
import java.util.Locale;
import static org.hunch.operations.GenerateUserOperations.dbOps;

public class DatabaseFunctions {
    private static final Logger LOGGER = Logger.getLogger(DatabaseFunctions.class);

    public static void generateRandomNewUser(){
        GenerateUserOperations genUser = new GenerateUserOperations();
        String userUid = genUser.generateRandomUUID();
        String phoneNumber = genUser.generateRandomNumber();
        String email = userUid+"@hunchmobile.com";
        InsertUsers ins = new InsertUsers(userUid, genUser.generateRandomUserName(), phoneNumber, email, "android", "2.56.3", genUser.generateRandomReferralCode().toUpperCase(Locale.ROOT), genUser.generateRandomAdId());
        ins.insertSignupLocationDetails("New Delhi","National Capital Territory of Delhi","India");
        int insertStatus = dbOps.insert("users", ins.toJsonObject(),"auto_id");
        LOGGER.info("Insert Status: "+insertStatus);
        LOGGER.info("Generated User Details : \n User UID: "+userUid+"\n Phone Number: "+phoneNumber+"\n Email: "+email);

        String query = "select *  from users where phone_number = '" + phoneNumber + "';";
        JSONArray arr =dbOps.executeQuery(query);
        if (arr.isEmpty()){
            LOGGER.info("No User Found with Phone Number : "+phoneNumber);
            return;
        }
        else {
            LOGGER.info("USER DETAILS : "+arr.getJSONObject(0).toString());
            FirebaseJWTManager.UserDetails usr = FirebaseJWTManager.getInstance().getUserDetails(arr.getJSONObject(0));
            String jwt = FirebaseJWTManager.getInstance().createFirebaseIdToken(usr);
            LOGGER.info("Generated JWT Token for User UID \n" +jwt);
            ThreadUtils.jwtToken.set(jwt);
        }
    }
}
