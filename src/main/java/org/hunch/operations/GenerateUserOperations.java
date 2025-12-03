package org.hunch.operations;

import com.github.javafaker.Faker;
import org.apache.log4j.Logger;
import org.hunch.apis.APIService;
import org.hunch.apis.BaseApi;
import org.hunch.constants.Config;
import org.hunch.dto.UserDetailsDTO;
import org.hunch.enums.core.RequestBodySchemaFileEnums;
import org.hunch.models.RequestBody;
import org.hunch.models.SetMultipleDps;
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
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    /**
     * Encapsulated user generation workflow that can be executed by multiple threads
     * @param threadId Thread identifier for logging
     */
    private static void generateUserWorkflow(int threadId) {
        try {
            LOGGER.info(String.format("[Thread-%d] Starting user generation workflow", threadId));

            // Initialize thread-local user DTO
            ThreadUtils.userDto.set(new UserDetailsDTO());

            // Execute user generation steps
            DatabaseFunctions.generateRandomNewUser();
            APIService.setupV2WithRandomData();
            APIService.setRandomMbti();
            APIService.uploadDps();
            DatabaseFunctions.updateUserImages();
            DatabaseFunctions.livenessDataSet();
            APIService.sendBirdUpdateDp();
            APIService.setupV2FinalCall();

            LOGGER.info(String.format("[Thread-%d] Successfully completed user generation for User ID: %s",
                    threadId, ThreadUtils.userDto.get().getUser_id()));
        } catch (Exception e) {
            LOGGER.error(String.format("[Thread-%d] Exception occurred during user generation: %s",
                    threadId, e.getMessage()), e);
            throw new RuntimeException("User generation failed in thread " + threadId, e);
        } finally {
            // Clean up thread-local variable
            ThreadUtils.userDto.remove();
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService = null;

        try {
            // Get thread count from system property, default to 1 if not specified
            int threadCount = Integer.parseInt(System.getProperty("thread.count", "1"));

            // Validate thread count
            if (threadCount < 1) {
                LOGGER.warn("Invalid thread count: " + threadCount + ". Setting to 1");
                threadCount = 1;
            }

            LOGGER.info(String.format("Starting user generation with %d parallel thread(s)", threadCount));

            // Create fixed thread pool
            executorService = Executors.newFixedThreadPool(threadCount);

            // Track successful and failed executions
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failureCount = new AtomicInteger(0);

            // Submit tasks to executor
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < threadCount; i++) {
                final int threadId = i + 1;
                Future<?> future = executorService.submit(() -> {
                    try {
                        generateUserWorkflow(threadId);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                        LOGGER.error(String.format("[Thread-%d] Failed to generate user", threadId), e);
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
                    successCount.get(), failureCount.get(), threadCount));

        } catch (NumberFormatException e) {
            LOGGER.error("Invalid thread.count system property. Must be a valid integer.", e);
        } catch (Exception e) {
            LOGGER.error("Exception occurred in main: " + e.getMessage(), e);
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

            // Close database connection pool
            dbConnection.closePool();
            LOGGER.info("Application shutdown complete");
        }
    }
}
