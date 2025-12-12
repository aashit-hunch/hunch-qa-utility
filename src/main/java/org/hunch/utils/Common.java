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
import java.nio.file.Files;
import java.nio.file.Path;
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

    public static void setUserDtoViaJsonObject(JSONObject jsonObject){
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
            ThreadUtils.userDto.set(dto);
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
        if(requestParams.getResponse().statusCode()==504){
            String graphQLOperationName = extractGraphQLOperationName(requestParams.getRequestBody());
            if(graphQLOperationName!=null){
                LOGGER.error("API Response 504 Gateway Timeout for GraphQL Operation: "+graphQLOperationName);
            }
            throw new RuntimeException("API failed with 504 Gateway Timeout");
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

}
