package org.hunch.utils;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.hunch.models.FirebaseServiceAccount;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Firebase Token Exchange Manager
 * Uses official Google Identity Toolkit API for token exchange
 *
 * Flow:
 * 1. Create Custom Token (Token A) using Firebase Admin SDK
 * 2. Exchange with Google's API for ID Token (Token B) - signed by Google
 * 3. Token B is ready for GraphQL server authentication
 */
public class FirebaseJWTManager {

    private static final Logger LOGGER = Logger.getLogger(FirebaseJWTManager.class);
    private static final String IDENTITY_TOOLKIT_URL =
            "https://identitytoolkit.googleapis.com/v1/accounts:signInWithCustomToken?key=";

    private static FirebaseJWTManager instance;
    private static FirebaseServiceAccount firebaseServiceAccount;
    private String firebaseWebApiKey;

    private FirebaseJWTManager() {
        // Private constructor for singleton
    }

    /**
     * Initialize Firebase Admin SDK
     */
    static {
        firebaseServiceAccount = new FirebaseServiceAccount().setFirebaseAccountProd();
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream serviceAccount = new ByteArrayInputStream(
                    firebaseServiceAccount.toString().getBytes(StandardCharsets.UTF_8)
            );

            try {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);

                // Extract Web API Key from service account JSON
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(firebaseServiceAccount.toString(), JsonObject.class);

                // Note: Web API Key should be set separately or extracted from Firebase config
                getInstance().firebaseWebApiKey = jsonObject.get("apiKey").getAsString();;

                if (getInstance().firebaseWebApiKey == null) {
                    LOGGER.warn("FIREBASE_WEB_API_KEY not set. Token exchange will fail.");
                }

            } catch (IOException e) {
                throw new RuntimeException("Exception Occurred while initializing Firebase: " + e.getMessage());
            }

            LOGGER.info("Firebase Token Exchange Manager Initialized Successfully");
        }
    }

    /**
     * Set Firebase Web API Key (required for token exchange)
     * Get this from Firebase Console > Project Settings > General > Web API Key
     */
    public void setFirebaseWebApiKey(String apiKey) {
        this.firebaseWebApiKey = apiKey;
        LOGGER.info("Firebase Web API Key configured");
    }

    /**
     * Get singleton instance
     */
    public static FirebaseJWTManager getInstance() {
        if (instance == null) {
            instance = new FirebaseJWTManager();
        }
        return instance;
    }

    /**
     * STEP 1: Generate Token A (Firebase Custom Token)
     * Uses Firebase Admin SDK
     *
     * @param userDetails User information
     * @return Token A (Custom Token signed by service account)
     * @throws FirebaseAuthException if token creation fails
     */
    public String generateTokenA(UserDetails userDetails) throws FirebaseAuthException {
        Map<String, Object> additionalClaims = new HashMap<>();

        if (userDetails.getEmail() != null && !userDetails.getEmail().isEmpty()) {
            additionalClaims.put("email", userDetails.getEmail());
        }
        if (userDetails.getPhoneNumber() != null && !userDetails.getPhoneNumber().isEmpty()) {
            additionalClaims.put("phoneNumber", userDetails.getPhoneNumber());
        }

        if (userDetails.getCustomClaims() != null && !userDetails.getCustomClaims().isEmpty()) {
            additionalClaims.putAll(userDetails.getCustomClaims());
        }

        String tokenA = FirebaseAuth.getInstance()
                .createCustomToken(userDetails.getUserId(), additionalClaims);
        LOGGER.info("TOKEN A Generated : "+ tokenA);
        LOGGER.info("Token A (Custom Token) generated for user: " + userDetails.getUserId());
        return tokenA;
    }

    /**
     * STEP 2: Exchange Token A for Token B (Firebase ID Token)
     * Uses Google's official Identity Toolkit API
     *
     * @param customToken Token A (Custom Token)
     * @return Token B (ID Token signed by Google)
     * @throws IOException if HTTP request fails
     * @throws InterruptedException if request is interrupted
     */
    public String exchangeForIdToken(String customToken) throws IOException, InterruptedException {
        if (firebaseWebApiKey == null || firebaseWebApiKey.isEmpty()) {
            throw new IllegalStateException(
                    "Firebase Web API Key not configured. Call setFirebaseWebApiKey() first.");
        }

        String url = IDENTITY_TOOLKIT_URL + firebaseWebApiKey;

        // Build request body
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("token", customToken);
        requestBody.addProperty("returnSecureToken", true);

        // Create HTTP client and request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString(), StandardCharsets.UTF_8))
                .build();

        // Send request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            LOGGER.error("Token exchange failed: " + response.body());
            throw new RuntimeException("Failed to exchange token. Status: " +
                    response.statusCode() + ", Body: " + response.body());
        }

        // Parse response to extract ID token
        Gson gson = new Gson();
        JsonObject responseJson = gson.fromJson(response.body(), JsonObject.class);
        String idToken = responseJson.get("idToken").getAsString();

        LOGGER.info("Token B (ID Token) successfully obtained from Google");
        return idToken;
    }

    /**
     * COMPLETE TOKEN EXCHANGE FLOW
     * Single method to perform the entire token exchange
     *
     * @param userDetails User information
     * @return TokenPair containing both Token A and Token B
     * @throws FirebaseAuthException if token generation fails
     * @throws IOException if token exchange fails
     * @throws InterruptedException if request is interrupted
     */
    public TokenPair performTokenExchange(UserDetails userDetails)
            throws FirebaseAuthException, IOException, InterruptedException {

        LOGGER.info("Starting token exchange for user: " + userDetails.getUserId());

        // Step 1: Generate Token A (Custom Token)
        String tokenA = generateTokenA(userDetails);

        // Step 2: Exchange for Token B (ID Token) via Google API
        String tokenB = exchangeForIdToken(tokenA);

        LOGGER.info("Token exchange completed successfully");
        return new TokenPair(tokenA, tokenB);
    }

    /**
     * Verify Token B (for testing/validation)
     * @param idToken Firebase ID Token
     * @return Decoded Firebase Token
     * @throws FirebaseAuthException if verification fails
     */
    public FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        LOGGER.info("Token verified successfully for user: " + decodedToken.getUid());
        return decodedToken;
    }

    /**
     * User Details Class
     */
    public static class UserDetails {
        private String userId;
        private String email;
        private String phoneNumber;
        private Map<String, Object> customClaims;

        private UserDetails() {
            this.customClaims = new HashMap<>();
        }

        public String getUserId() { return userId; }
        public String getEmail() { return email; }
        public String getPhoneNumber() { return phoneNumber; }
        public Map<String, Object> getCustomClaims() { return customClaims; }

        public static class Builder {
            private UserDetails userDetails;

            public Builder() {
                userDetails = new UserDetails();
            }

            public Builder setUserId(String userId) {
                userDetails.userId = userId;
                return this;
            }

            public Builder setEmail(String email) {
                userDetails.email = email;
                return this;
            }

            public Builder setPhoneNumber(String phoneNumber) {
                userDetails.phoneNumber = phoneNumber;
                return this;
            }

            public Builder addCustomClaim(String key, Object value) {
                userDetails.customClaims.put(key, value);
                return this;
            }

            public UserDetails build() {
                if (userDetails.userId == null || userDetails.userId.isEmpty()) {
                    throw new IllegalArgumentException("userId is required");
                }
                return userDetails;
            }
        }
    }

    /**
     * Token Pair Class
     */
    public static class TokenPair {
        private final String tokenA;
        private final String tokenB;

        public TokenPair(String tokenA, String tokenB) {
            this.tokenA = tokenA;
            this.tokenB = tokenB;
        }

        public String getTokenA() { return tokenA; }
        public String getTokenB() { return tokenB; }

        @Override
        public String toString() {
            return "TokenPair{\n" +
                    "  tokenA (Custom Token) = " + tokenA + "\n" +
                    "  tokenB (ID Token - Google Signed) = " + tokenB + "\n" +
                    "}";
        }
    }

    /**
     * Example usage
     */
    public static void main(String[] args) {
        try {
            FirebaseJWTManager manager = FirebaseJWTManager.getInstance();

            // IMPORTANT: Set your Firebase Web API Key
            // Get this from: Firebase Console > Project Settings > General > Web API Key
            manager.setFirebaseWebApiKey("AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

            System.out.println("\n" + "=".repeat(70));
            System.out.println("FIREBASE TOKEN EXCHANGE - Official Google API");
            System.out.println("=".repeat(70));

            // Build user details
            UserDetails userDetails = new UserDetails.Builder()
                    .setUserId("1d315ef1-2571-4314-9456-b00452700de19")
                    .setEmail("1d315ef1-2571-4314-9456-b00452700de19@hunchmobile.com")
                    .setPhoneNumber("+919311384915")
                    .addCustomClaim("role", "user")
                    .build();

            System.out.println("\n📋 User Details:");
            System.out.println("   User ID: " + userDetails.getUserId());
            System.out.println("   Email: " + userDetails.getEmail());
            System.out.println("   Phone: " + userDetails.getPhoneNumber());

            // Perform token exchange
            System.out.println("\n🔄 Performing Token Exchange...");
            TokenPair tokens = manager.performTokenExchange(userDetails);

            System.out.println("\n" + "=".repeat(70));
            System.out.println("TOKEN A (Custom Token - Service Account Signed)");
            System.out.println("=".repeat(70));
            System.out.println(tokens.getTokenA());

            System.out.println("\n" + "=".repeat(70));
            System.out.println("TOKEN B (ID Token - Google Signed) ✅");
            System.out.println("=".repeat(70));
            System.out.println(tokens.getTokenB());

            // Verify Token B
            System.out.println("\n🔍 Verifying Token B with Firebase...");
            FirebaseToken verifiedToken = manager.verifyIdToken(tokens.getTokenB());
            System.out.println("✅ Token verified successfully!");
            System.out.println("   User ID: " + verifiedToken.getUid());
            System.out.println("   Email: " + verifiedToken.getEmail());

            System.out.println("\n" + "=".repeat(70));
            System.out.println("✅ Token B is ready for GraphQL authentication!");
            System.out.println("=".repeat(70));

        } catch (Exception e) {
            LOGGER.error("Error during token exchange", e);
            e.printStackTrace();
        }
    }
}