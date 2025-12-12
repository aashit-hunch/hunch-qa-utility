package org.hunch.operations;

import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.hunch.apis.APIService;
import org.hunch.constants.Config;
import org.hunch.constants.GlobalData;
import org.hunch.dto.UserDetailsDTO;
import org.hunch.enums.core.UserOperations;
import org.hunch.utils.Common;
import org.hunch.utils.CryptoUtility;
import org.hunch.utils.PrintResultOutput;
import org.hunch.utils.database.DBConfig;
import org.hunch.utils.database.DatabaseFunctions;
import org.hunch.utils.database.DatabaseOperations;
import org.hunch.utils.database.PostgresDBConnections;
import org.hunch.utils.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GenerateUserOperations {
    public static PostgresDBConnections dbConnection = new PostgresDBConnections();
    public static DatabaseOperations dbOps;
    private static final Logger LOGGER = Logger.getLogger(GenerateUserOperations.class);
    private static final Set<UserDetailsDTO> generatedSuccessUserUids = ConcurrentHashMap.newKeySet();
    private static final Set<UserDetailsDTO> generatedFailedUserUids = ConcurrentHashMap.newKeySet();
    private static final Set<UserDetailsDTO> generatedSenderUids = ConcurrentHashMap.newKeySet();
    private static final Set<UserDetailsDTO> generatedReceiverUids = ConcurrentHashMap.newKeySet();

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
        boolean isValid = false;
        String uuid = "";
        while (!isValid) {
            uuid = UUID.randomUUID().toString();
            String query = "select user_uid  from users where user_uid = '" + uuid + "';";
            int count = dbOps.executeQuery(query).length();
            if (count == 0) {
                isValid = true;
                break;
            }
        }
        return uuid;
    }

    public String generateRandomNumber() {
        LOGGER.info("Generating Random Phone Number");
        boolean isValid = false;
        Long number = Long.MIN_VALUE;
        while (!isValid) {
            number = Faker.instance().number().numberBetween(6000000000L, 9999999999L);
            String query = "select user_uid  from users where phone_number = '+91" + number + "';";
            int count = dbOps.executeQuery(query).length();
            if (count == 0) {
                isValid = true;
                break;
            }
        }
        return "+91" + number;
    }

    public String generateRandomAdId() {
        LOGGER.info("Generating Random Adjust Ad ID");
        boolean isValid = false;
        String adjust_adid = "";
        while (!isValid) {
            adjust_adid = Common.generateRandomAlphaNumeric(32);
            String query = "select user_uid  from users where adjust_adid = '" + adjust_adid + "';";
            int count = dbOps.executeQuery(query).length();
            if (count == 0) {
                isValid = true;
                break;
            }
        }
        return adjust_adid;
    }

    public String generateRandomReferralCode() {
        LOGGER.info("Generating Random Referral Code");
        boolean isValid = false;
        String referral_code = "";
        while (!isValid) {
            referral_code = Common.generateRandomAlphaNumeric(8);
            String query = "select user_uid  from users where referral_code = '" + referral_code + "';";
            int count = dbOps.executeQuery(query).length();
            if (count == 0) {
                isValid = true;
                break;
            }
        }
        return referral_code;
    }

    public String generateRandomUserName() {
        LOGGER.info("Generating Random Username");
        boolean isValid = false;
        String username = "";
        while (!isValid) {
            username = Faker.instance().name().firstName() + Faker.instance().name().lastName();
            username.replaceAll("'","");
            String query = "select user_uid  from users where username = '" + username + "';";
            int count = dbOps.executeQuery(query).length();
            if (count == 0) {
                isValid = true;
                break;
            }
        }
        return username;
    }

    /**
     * Encapsulated user generation workflow that can be executed by multiple threads
     *
     */
    private static void generateUserWorkflow() {
        try {
            // Initialize thread-local user DTO
            ThreadUtils.userDto.set(new UserDetailsDTO());

            // Execute user generation steps
            DatabaseFunctions.generateRandomNewUser();
            APIService.setupV2WithSpecificData(GlobalData.JOURNEY);

            switch (GlobalData.JOURNEY) {
                case mbti -> {
                    APIService.setupV2WithRandomData();
                    APIService.setRandomMbti();
                }
                case images -> {
                    APIService.setupV2WithRandomData();
                    APIService.setRandomMbti();
                    APIService.uploadDps();
                    DatabaseFunctions.updateUserImages();
                    DatabaseFunctions.livenessDataSet();
                    APIService.sendBirdUpdateDp();
                    APIService.setupV2FinalCall();
                    APIService.updateGeoLocation();
                    //APIService.getUnifiedFeed();
                    DatabaseFunctions.increaseCrushLimit(ThreadUtils.userDto.get().getUser_id(), 50);
                }
                default -> {
                    // No additional steps
                }
            }

            UserDetailsDTO userDto = ThreadUtils.userDto.get();
            if (userDto != null && userDto.getUser_id() != null) {
                generatedSuccessUserUids.add(userDto);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred during user generation:"+ e.getMessage());
            UserDetailsDTO failedUserDto = ThreadUtils.userDto.get();
            if (failedUserDto != null && failedUserDto.getUser_id() != null) {
                generatedFailedUserUids.add(failedUserDto);
            }
            throw new RuntimeException("User generation failed  " , e);
        } finally {
            // Clean up thread-local variable
            ThreadUtils.userDto.remove();
        }
    }

    private static void generateWaveCrush(boolean isCrush,JSONObject sender, JSONObject receiver,boolean isAccepted) {
        try {
            generatedSenderUids.add(Common.setUserDtoViaJsonObject(sender));
            generatedReceiverUids.add(Common.setUserDtoViaJsonObject(receiver));
            if(!isCrush) DatabaseFunctions.makeUserPremium(sender.getString("user_uid"));
            else DatabaseFunctions.increaseCrushLimit(sender.getString("user_uid"),GlobalData.THREAD_COUNT);
            //DatabaseFunctions.deleteWaveCrush(sender.getString("user_uid"),receiver.getString("user_uid"));
            if(APIService.sendBirdGetUser(receiver.getString("user_uid")).statusCode()==400){
                APIService.sendBirdCreateUser(receiver);
            }
            if(APIService.sendBirdGetUser(sender.getString("user_uid")).statusCode()==400){
                APIService.sendBirdCreateUser(sender);
            }
            String senderJwt = Common.generateFirebaseToken(sender);
            String receiverJwt = Common.generateFirebaseToken(receiver);
            Response response =APIService.initiateWave(receiver.getString("user_uid"),isCrush,receiver.getString("dp"),"",senderJwt);

            if(isAccepted){
                Response resp = APIService.acceptWave(isCrush,response.jsonPath().getString("data.initiateWave.id"),receiverJwt);
                APIService.sendBirdSendMessage(resp.jsonPath().getString("data.confirmMatchV2.channelUrl"),receiver.getString("user_uid"));
            }

        } catch (Exception e) {
            LOGGER.error("Exception occurred during wave/crush generation:"+ e.getMessage());
            throw new RuntimeException("Wave/Crush generation failed  " , e);
        }

    }
    private static void userGenerationManager(int numberOfUsers, JSONArray jsonArray, JSONObject jsonObject) {
        ExecutorService executorService = null;

        try {

            LOGGER.info(String.format("Starting Data generation with %d parallel thread(s)", numberOfUsers));

            // Create fixed thread pool
            executorService = Executors.newFixedThreadPool(numberOfUsers);

            // Track successful and failed executions
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failureCount = new AtomicInteger(0);

            // Submit tasks to executor
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < numberOfUsers; i++) {
                int finalI = i;
                Future<?> future = executorService.submit(() -> {
                    try {
                        switch (GlobalData.USER_OPERATION_TYPE){
                            case GENERATE_USER -> generateUserWorkflow();
                            case GENERATE_WAVE_SENT ->
                            {
                                generateWaveCrush(false,jsonObject,jsonArray.getJSONObject(finalI),false);
                            }
                            case GENERATE_CRUSH_SENT -> {
                                generateWaveCrush(true,jsonObject,jsonArray.getJSONObject(finalI),false);
                            }
                            case GENERATE_WAVE_RECEIVED -> {
                                generateWaveCrush(false,jsonArray.getJSONObject(finalI),jsonObject,false);
                            }
                            case GENERATE_CRUSH_RECEIVED -> {
                                generateWaveCrush(true,jsonArray.getJSONObject(finalI),jsonObject,false);
                            }
                            case GENERATE_CRUSH_SENT_ACCEPTED ->{
                                generateWaveCrush(true,jsonObject,jsonArray.getJSONObject(finalI),true);
                            }
                            case GENERATE_CRUSH_RECEIVED_ACCEPTED -> {
                                generateWaveCrush(true,jsonArray.getJSONObject(finalI),jsonObject,true);
                            }
                            default -> LOGGER.warn("Unknown operation type: " + GlobalData.USER_OPERATION);
                        }

                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                        LOGGER.error("Failed to generate user", e);
                    }
                });
                futures.add(future);
            }

            // Wait for all tasks to complete
            LOGGER.info("Waiting for all threads to complete...");
            for (Future<?> future : futures) {
                try {
                    future.get(); // This will block until the task completes
                } catch (ExecutionException e) {
                    LOGGER.error("Task execution failed", e.getCause());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.error("Thread interrupted while waiting for task completion", e);
                }
            }

            // Log summary
            LOGGER.info(String.format("User generation completed. Success: %d, Failed: %d, Total: %d",
                    successCount.get(), failureCount.get(), numberOfUsers));

            if(!generatedSuccessUserUids.isEmpty() || !generatedFailedUserUids.isEmpty()){
                // Print success and failed user IDs in boxes
                PrintResultOutput.printBox("Generated Success User", generatedSuccessUserUids);
                PrintResultOutput.printBox("Generated Failed User", generatedFailedUserUids);
                DatabaseFunctions.rollBackFailedUserCreation(generatedFailedUserUids);
            }
            if(!generatedSenderUids.isEmpty() && !generatedReceiverUids.isEmpty()){
                PrintResultOutput.printBox("Wave/Crush Sender User", generatedSenderUids);
                PrintResultOutput.printBox("Wave/Crush Receiver User", generatedReceiverUids);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred in while performing operation : " + e.getMessage(), e);
        } finally {
            // Shutdown executor service
            if (executorService != null) {
                LOGGER.info("Shutting down executor service...");
                executorService.shutdown();
                try {
                    if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                        LOGGER.warn("Executor did not terminate in time. Forcing shutdown...");
                        executorService.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    LOGGER.error("Interrupted while waiting for executor shutdown", e);
                    executorService.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }

            // Clear the user ID collections for next run
            generatedSuccessUserUids.clear();
            generatedFailedUserUids.clear();
            generatedSenderUids.clear();
            generatedReceiverUids.clear();

        }
    }

    public static void main(String[] args) {

        // Configure Log4j logging level based on GlobalData.ENABLE_INFO_LOGS flag
        LoggerConfig.configure();

        try {
            // System Properties that can be set during execution:
            //operation : String = generateUser/ generateWave/ generateCrush
                //gen.user : = To generate new User
                //gen.wave.sent : = To generate Wave Sent for user
                //gen.wave.received : = To generate Wave Received for user
                //gen.crush.sent : = To generate Crush Sent for user
                //gen.crush.received : = To generate Crush Received for user
                //gen.crush.sent.accepted : = To generate Crush Sent for user in accepted state
                //gen.crush.received.accepted : = To generate Crush Received for user in accepted state
            //user.count : int = Number of Users/Wave/Crush to generate
            //phone.number : String = Number against which operations need to be performed for wave/crush
            //user.gender : Gender Enum
            //user.preference : Gender Enum
            //user.custom : Boolean = true/false (If true, user provided data will be used for user generation)

            // Get User count from system property, default to 1 if not specified

            int threadCount = GlobalData.THREAD_COUNT;
            // Validate User count
            if (threadCount < 1) {
                LOGGER.warn("Invalid User count: " + threadCount + ". Setting to 1");
                threadCount = 1;
            }
            else if(threadCount>2 && GlobalData.USER_OPERATION_TYPE== UserOperations.GENERATE_USER){
                LOGGER.warn("User count too high: " + threadCount + ". Setting to 1 for user generation to avoid resource exhaustion.");
                threadCount = 2;
            }
            else if(threadCount>5){
                LOGGER.warn("User count too high: " + threadCount + ". Setting to 5 to avoid resource exhaustion.");
                threadCount = 5;
            }
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            switch (GlobalData.USER_OPERATION_TYPE){
                case GENERATE_USER -> userGenerationManager(threadCount,jsonArray,jsonObject);
                case GENERATE_WAVE_SENT,GENERATE_CRUSH_SENT,GENERATE_CRUSH_RECEIVED,GENERATE_WAVE_RECEIVED,GENERATE_CRUSH_SENT_ACCEPTED,GENERATE_CRUSH_RECEIVED_ACCEPTED ->
                {
                    JSONArray userData =DatabaseFunctions.getUserDataByPhone(GlobalData.PHONE_NUMBER);
                    if(userData.isEmpty()){
                        generateUserWorkflow();
                        userData =DatabaseFunctions.getUserDataByPhone(GlobalData.PHONE_NUMBER);
                    }
                    ThreadUtils.userDto.set(Common.setUserDtoViaJsonObject(userData.getJSONObject(0)));
                    jsonArray =DatabaseFunctions.getGenderPreferredData();
                    jsonObject = userData.getJSONObject(0);
                    userGenerationManager(threadCount,jsonArray,jsonObject);

                }
                default -> LOGGER.warn("Unknown operation type: " + GlobalData.USER_OPERATION);
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid user.count system property. Must be a valid integer.", e);
        } finally {
            // Shutdown Rest-Assured HTTP client to terminate httpclient threads
            shutdownRestAssured();

            // Close database connection pool (this also deregisters JDBC drivers)
            dbConnection.closePool();

            LOGGER.info("All resources released. Forcing application exit...");

            // Force JVM exit to terminate lingering non-daemon threads
            // This is necessary because PostgreSQL-JDBC-Cleaner and httpclient threads
            // are non-daemon and don't respond to interruption
            System.exit(0);
        }
    }

    /**
     * Properly shutdown Rest-Assured HTTP client to terminate background threads
     */
    private static void shutdownRestAssured() {
        try {
            LOGGER.info("Shutting down Rest-Assured HTTP client...");

            // Reset Rest-Assured configuration
            io.restassured.RestAssured.reset();

            // Force garbage collection to clean up HTTP client resources
            System.gc();

            LOGGER.info("Rest-Assured HTTP client shut down successfully");
        } catch (Exception e) {
            LOGGER.error("Error shutting down Rest-Assured", e);
        }
    }
}
