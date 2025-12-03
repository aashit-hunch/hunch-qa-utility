package org.hunch.utils;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import tools.jackson.databind.ObjectMapper;

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

    public static JSONArray getUserData(){
        try{
            String json = Files.readString(Path.of(System.getProperty("user.dir")+"/src/main/resources/userData.json"));
            JSONArray obj = new JSONArray(json);
            List<Object> list = obj.toList();;
            Collections.shuffle(list);
            obj = new JSONArray(list);
            return obj;
        }
        catch (Exception e){
            throw  new RuntimeException("Exception occurred while reading user data json: "+e.getMessage());
        }
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

}
