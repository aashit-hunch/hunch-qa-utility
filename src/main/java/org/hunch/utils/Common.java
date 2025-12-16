package org.hunch.utils;

import com.google.firebase.auth.FirebaseAuthException;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.hunch.core.RequestParams;
import org.hunch.dto.UserDetailsDTO;
import org.hunch.enums.Ethnicity;
import org.hunch.enums.Gender;
import org.hunch.enums.DesiredRelationshipType;
import org.json.JSONArray;
import org.json.JSONObject;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Common {
    private static final Logger LOGGER = Logger.getLogger(Common.class);

    public static final ObjectMapper mapper = new ObjectMapper();


    private static final Random RANDOM = new Random();

    public static <T extends Enum<?>> T randomEnum(Class<T> enumClass){
        T[] constants = enumClass.getEnumConstants();
        return constants[RANDOM.nextInt(constants.length)];
    }

    public static <T extends Enum<?>> List<T> getRandomEnumList(Class<T> enumClass, int maxSize) {

        T[] constants = enumClass.getEnumConstants();
        if(maxSize>constants.length){
            maxSize = constants.length;
            LOGGER.info("Requested size is greater than enum constants size. Setting maxSize to "+maxSize);
        }
        List<T> constantsList = new ArrayList<>(Arrays.asList(constants));
        Collections.shuffle(constantsList, RANDOM);

        return constantsList.subList(0, maxSize);
    }

    public static String generateRandomAlphaNumeric(int length) {
        String ALPHA_NUMERIC = "0123456789abcdef";
        SecureRandom RANDOM = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(ALPHA_NUMERIC.length());
            sb.append(ALPHA_NUMERIC.charAt(index));
        }
        return sb.toString();
    }

    public static String getCurrentTimestamp(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return LocalDateTime.now().format(formatter);
    }

    public static String getFutureTimestamp(int days) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime futureDate = LocalDateTime.now().plusDays(days);
        return futureDate.format(formatter);
    }

    public static String getPastTimestamp(int days) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime pastDate = LocalDateTime.now().minusDays(days);
        return pastDate.format(formatter);
    }

    public static JSONArray getUserData(){
        try{
            String json = Files.readString(Path.of(System.getProperty("user.dir")+"/src/main/resources/userData.json"));
            JSONArray obj = new JSONArray(json);
            obj= shuffleJsonArray(obj);
            return obj;
        }
        catch (Exception e){
            throw  new RuntimeException("Exception occurred while reading user data json: "+e.getMessage());
        }
    }
    public static JSONArray shuffleJsonArray(JSONArray array){
        List<Object> list = array.toList();
        Collections.shuffle(list);
        return new JSONArray(list);
    }

    public static JSONObject getLivenessData(){
        try{
            String json = Files.readString(Path.of(System.getProperty("user.dir")+"/src/main/resources/liveness.json"));
            JSONObject obj = new JSONObject(json);
            return obj;
        }
        catch (Exception e){
            throw  new RuntimeException("Exception occurred while reading liveness data json: "+e.getMessage());
        }
    }

    public static UserDetailsDTO setUserDtoViaJsonObject(JSONObject jsonObject){
        try{
            UserDetailsDTO dto = new UserDetailsDTO();
            dto.setUser_id(jsonObject.getString("user_uid"));
            dto.setEmail(jsonObject.getString("email"));
            dto.setPhone_number(jsonObject.getString("phone_number"));
            dto.setMainDpUrl(jsonObject.getString("dp"));
            dto.setGender(Gender.fromString(jsonObject.getString("gender")));
            dto.setOtherDpUrls(
                jsonObject.getJSONArray("multiple_dps")
                .toList()
                .stream()
                .map(Object::toString)
                .toList());
            dto.setEthnicity(Ethnicity.fromString(jsonObject.getString("ethnicity")));
            dto.setDesired_relationship_types(
                jsonObject.getJSONArray("desired_relationship_type")
                    .toList()
                    .stream()
                    .map(Object::toString)
                    .map(DesiredRelationshipType::fromString)
                    .toList());
            dto.setDating_preferences(
                    jsonObject.getJSONArray("dating_preference")
                            .toList()
                            .stream()
                            .map(Object::toString)
                            .map(Gender::fromString)
                            .toList());
            return dto;
        }
        catch (Exception e){
            throw new RuntimeException("Exception occurred while setting UserDto from JSONArray : "+e.getMessage());
        }
    }

    /**
     * Generate Firebase Token from JSON Object of Database record
     * @param jsonObject
     * @return
     */
    public static String generateFirebaseToken(JSONObject jsonObject) throws IOException, FirebaseAuthException, InterruptedException {
        FirebaseJWTManager.UserDetails ud = new FirebaseJWTManager.UserDetails.Builder().setUserId(jsonObject.getString("user_uid"))
                .setEmail(jsonObject.getString("email"))
                .setPhoneNumber(jsonObject.getString("phone_number"))
                .build();

        FirebaseJWTManager.TokenPair tk =FirebaseJWTManager.getInstance().performTokenExchange(ud);
        return tk.getTokenB();
    }

    public static void validateApiStatusCode(RequestParams requestParams){
        if(requestParams.getResponse().statusCode()==504 || requestParams.getResponse().statusCode()==502 || requestParams.getResponse().statusCode()==503){
            String requestBody = requestParams.getRequestBody() != null ? requestParams.getRequestBody().toString() : null;
            String graphQLOperationName = extractGraphQLOperationName(requestBody);
            if(graphQLOperationName!=null){
                LOGGER.error("API Response "+requestParams.getResponse().statusCode()+" "+requestParams.getResponse().statusLine()+" for GraphQL Operation: "+graphQLOperationName);
            }
            throw new RuntimeException("API failed with "+requestParams.getResponse().statusCode()+" "+requestParams.getResponse().statusLine());
        }
    }
    /**
     * Extracts the GraphQL operation name (after 'query' or 'mutation' and before '(') from the request body.
     * Case-insensitive for 'query' and 'mutation'. Returns null if not found.
     */
    public static String extractGraphQLOperationName(String requestBody) {
        if (requestBody == null) return null;
        // (?i) makes the regex case-insensitive
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)\\b(query|mutation)\\b\\s+(\\w+)\\s*\\(");
        java.util.regex.Matcher matcher = pattern.matcher(requestBody);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return null;
    }

    /**
     * Downloads an image from a provided URL and stores it in src/main/resources/userImages directory
     *
     * @param imageUrl The URL of the image to download
     * @param fileName The name to save the file as (including extension, e.g., "user_profile.jpg")
     * @return Path to the saved image file
     * @throws IOException If download or file write fails
     */
    public static Path downloadImage(String imageUrl, String fileName) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty");
        }
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        LOGGER.info("Downloading image from URL: " + imageUrl);

        // Create target directory path
        String projectDir = System.getProperty("user.dir");
        Path targetDirectory = Paths.get(projectDir, "src", "main", "resources", "userImages");


        try {
            // Create directory if it doesn't exist
            if (!Files.exists(targetDirectory)) {
                LOGGER.info("Creating directory: " + targetDirectory);
                Files.createDirectories(targetDirectory);
            }

            // Create target file path
            Path targetFilePath = targetDirectory.resolve(fileName+".jpeg");

            // Open connection to the URL
            URL url = new URL(imageUrl);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10000); // 10 seconds
            connection.setReadTimeout(30000);    // 30 seconds

            // Set user agent to avoid being blocked by some servers
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

            // Download the image
            try (InputStream inputStream = connection.getInputStream();
                 OutputStream outputStream = Files.newOutputStream(targetFilePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalBytesRead = 0;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                }

                LOGGER.info(String.format("Image downloaded successfully: %s (%d bytes)", targetFilePath, totalBytesRead));
            }

            return targetFilePath;

        } catch (IOException e) {
            LOGGER.error("Failed to download image from: " + imageUrl, e);
            throw new RuntimeException("Failed to download image from URL: " + imageUrl, e);
        }
    }

    /**
     * Downloads an image from a provided URL and stores it in src/main/resources/userImages directory
     * with automatic file extension detection from the URL
     *
     * @param imageUrl The URL of the image to download
     * @param fileNameWithoutExtension The base name to save the file as (without extension)
     * @return Path to the saved image file
     * @throws IOException If download or file write fails
     */
    public static Path downloadImageWithAutoExtension(String imageUrl, String fileNameWithoutExtension)  {
        // Extract file extension from URL
        String extension = extractFileExtension(imageUrl);
        String fileName = fileNameWithoutExtension + extension;
        return downloadImage(imageUrl, fileName);
    }

    /**
     * Extracts file extension from URL
     *
     * @param url The URL to extract extension from
     * @return File extension including the dot (e.g., ".jpg", ".png"), or ".jpg" as default
     */
    private static String extractFileExtension(String url) {
        if (url == null || url.trim().isEmpty()) {
            return ".jpg"; // Default extension
        }

        // Remove query parameters
        int queryIndex = url.indexOf('?');
        if (queryIndex != -1) {
            url = url.substring(0, queryIndex);
        }

        // Extract extension
        int lastDotIndex = url.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex < url.length() - 1) {
            String extension = url.substring(lastDotIndex);
            // Validate extension (should be 3-4 characters like .jpg, .png, .jpeg, .webp)
            if (extension.matches("\\.[a-zA-Z]{3,4}")) {
                return extension.toLowerCase();
            }
        }

        return ".jpg"; // Default extension
    }

    /**
     * Deletes all files inside the src/main/resources/userImages directory
     * The directory itself is preserved for future downloads
     *
     * @return Number of files deleted
     * @throws IOException If deletion fails
     */
    public static int clearUserImagesDirectory() {
        String projectDir = System.getProperty("user.dir");
        Path userImagesDir = Paths.get(projectDir, "src", "main", "resources", "userImages");

        if (!Files.exists(userImagesDir)) {
            LOGGER.info("User images directory does not exist: " + userImagesDir);
            return 0;
        }

        if (!Files.isDirectory(userImagesDir)) {
            //
        }

        LOGGER.info("Clearing all files from directory: " + userImagesDir);

        int deletedCount = 0;
        int failedCount = 0;

        try (var stream = Files.list(userImagesDir)) {
            var files = stream.filter(Files::isRegularFile).toList();

            for (Path file : files) {
                try {
                    Files.delete(file);
                    deletedCount++;
                    LOGGER.info("Deleted file: " + file.getFileName());
                } catch (IOException e) {
                    failedCount++;
                    LOGGER.error("Failed to delete file: " + file.getFileName(), e);
                }
            }
        } catch (IOException e) {
            //
        }

        if (failedCount > 0) {
            LOGGER.warn(String.format("Deletion completed with errors. Deleted: %d, Failed: %d", deletedCount, failedCount));
        } else {
            LOGGER.info(String.format("Successfully deleted %d file(s) from user images directory", deletedCount));
        }

        return deletedCount;
    }

    /**
     * Deletes all files and subdirectories inside the src/main/resources/userImages directory
     * and optionally removes the directory itself
     *
     * @param removeDirectory If true, removes the directory itself; if false, keeps the empty directory
     * @return Number of items (files and directories) deleted
     * @throws IOException If deletion fails
     */
    public static int clearUserImagesDirectory(boolean removeDirectory) throws IOException {
        String projectDir = System.getProperty("user.dir");
        Path userImagesDir = Paths.get(projectDir, "src", "main", "resources", "userImages");

        if (!Files.exists(userImagesDir)) {
            LOGGER.info("User images directory does not exist: " + userImagesDir);
            return 0;
        }

        if (!Files.isDirectory(userImagesDir)) {
            throw new IOException("Path exists but is not a directory: " + userImagesDir);
        }

        LOGGER.info("Clearing all contents from directory: " + userImagesDir);

        int deletedCount = deleteDirectoryContents(userImagesDir);

        if (removeDirectory) {
            try {
                Files.delete(userImagesDir);
                deletedCount++;
                LOGGER.info("Deleted directory: " + userImagesDir);
            } catch (IOException e) {
                LOGGER.error("Failed to delete directory: " + userImagesDir, e);
                throw e;
            }
        }

        LOGGER.info(String.format("Successfully deleted %d item(s)", deletedCount));
        return deletedCount;
    }

    /**
     * Recursively deletes all contents of a directory (but not the directory itself)
     *
     * @param directory The directory to clear
     * @return Number of items deleted
     * @throws IOException If deletion fails
     */
    private static int deleteDirectoryContents(Path directory) throws IOException {
        int deletedCount = 0;

        try (var stream = Files.list(directory)) {
            var items = stream.toList();

            for (Path item : items) {
                if (Files.isDirectory(item)) {
                    // Recursively delete subdirectory contents first
                    deletedCount += deleteDirectoryContents(item);
                    // Then delete the empty subdirectory
                    Files.delete(item);
                    deletedCount++;
                    LOGGER.info("Deleted directory: " + item.getFileName());
                } else {
                    // Delete file
                    Files.delete(item);
                    deletedCount++;
                    LOGGER.info("Deleted file: " + item.getFileName());
                }
            }
        }

        return deletedCount;
    }

}
