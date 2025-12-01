package org.hunch.operations;

import com.github.javafaker.Faker;
import org.apache.log4j.Logger;
import org.hunch.apis.APIService;
import org.hunch.apis.BaseApi;
import org.hunch.constants.Config;
import org.hunch.enums.core.RequestBodySchemaFileEnums;
import org.hunch.models.RequestBody;
import org.hunch.models.SetupUserV2;
import org.hunch.models.db.InsertUsers;
import org.hunch.utils.Common;
import org.hunch.utils.CryptoUtility;
import org.hunch.utils.FirebaseJWTManager;
import org.hunch.utils.database.DBConfig;
import org.hunch.utils.database.DatabaseFunctions;
import org.hunch.utils.database.DatabaseOperations;
import org.hunch.utils.database.PostgresDBConnections;
import org.json.JSONArray;

import org.hunch.utils.*;

import java.util.Locale;
import java.util.UUID;

public class GenerateUserOperations {
    public static PostgresDBConnections dbConnection = new PostgresDBConnections();
    public static DatabaseOperations dbOps;
    private static final Logger LOGGER = Logger.getLogger(GenerateUserOperations.class);

    static {
        DBConfig config = new DBConfig(CryptoUtility.decrypt(Config.DB_HOST),
                5432,
                CryptoUtility.decrypt(Config.DB_USER),
                CryptoUtility.decrypt(Config.DB_PASS),
                CryptoUtility.decrypt(Config.DB_NAME));

        dbConnection.makeConnection(config);
        dbOps = new DatabaseOperations(dbConnection);
    }

    public String generateRandomUUID() {
        LOGGER.info("Generating Random UUID");
        boolean isValid =false;
        String uuid = "";
        while (!isValid){
            uuid = UUID.randomUUID().toString();
            String query = "select user_uid  from users where user_uid = '" + uuid + "';";
            int count = dbOps.executeQuery(query).length();
            if (count == 0) {
                isValid=true;
                break;
            }
        }
        return uuid;
    }

    public String generateRandomNumber() {
        LOGGER.info("Generating Random Phone Number");
        boolean isValid =false;
        Long number =Long.MIN_VALUE;
        while (!isValid){
            number = Faker.instance().number().numberBetween(6000000000L,9999999999L);
            String query = "select user_uid  from users where phone_number = '+91" + number + "';";
            int count = dbOps.executeQuery(query).length();
            if (count == 0) {
                isValid=true;
                break;
            }
        }
        return "+91"+number;
    }

    public String generateRandomAdId(){
        LOGGER.info("Generating Random Adjust Ad ID");
        boolean isValid =false;
        String adjust_adid = "";
        while (!isValid){
            adjust_adid = Common.generateRandomAlphaNumeric(32);
            String query = "select user_uid  from users where adjust_adid = '" + adjust_adid + "';";
            int count = dbOps.executeQuery(query).length();
            if (count == 0) {
                isValid=true;
                break;
            }
        }
        return adjust_adid;
    }

    public String generateRandomReferralCode(){
        LOGGER.info("Generating Random Referral Code");
        boolean isValid =false;
        String referral_code = "";
        while (!isValid){
            referral_code = Common.generateRandomAlphaNumeric(8);
            String query = "select user_uid  from users where referral_code = '" + referral_code + "';";
            int count = dbOps.executeQuery(query).length();
            if (count == 0) {
                isValid=true;
                break;
            }
        }
        return referral_code;
    }

    public String generateRandomUserName(){
        LOGGER.info("Generating Random Username");
        boolean isValid =false;
        String username = "";
        while (!isValid){
            username = Faker.instance().name().firstName()+Faker.instance().name().lastName();
            String query = "select user_uid  from users where username = '" + username + "';";
            int count = dbOps.executeQuery(query).length();
            if (count == 0) {
                isValid=true;
                break;
            }
        }
        return username;
    }

    public static void main(String[] args) {
       try {
           GenerateUserOperations genUser = new GenerateUserOperations();
           //DatabaseFunctions.generateRandomNewUser();
           String num = genUser.generateRandomNumber();
           APIService.sendOtp(num);
           APIService.verifyOtp(num);
           //APIService.setupV2WithRandomData();
           //APIService.setRandomMbti();

       }
       finally {
              dbConnection.closePool();
       }
    }
}
