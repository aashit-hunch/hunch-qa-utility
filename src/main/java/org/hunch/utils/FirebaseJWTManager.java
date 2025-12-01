package org.hunch.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.apache.log4j.Logger;
import org.hunch.models.FirebaseServiceAccount;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.*;
public class FirebaseJWTManager {

    private static final Logger LOGGER = Logger.getLogger(FirebaseJWTManager.class);
    private static FirebaseJWTManager instance;
    private static FirebaseServiceAccount firebaseServiceAccount;
    private ServiceAccountCredentials serviceAccountCredentials;
    private String projectId;
    private String privateKeyId;

    private FirebaseJWTManager() {
        // Private constructor for singleton
    }

    // Initialize Firebase Admin SDK using JSON string
    static {
        firebaseServiceAccount = new FirebaseServiceAccount().setFirebaseAccount();
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream serviceAccount = new ByteArrayInputStream(
                    firebaseServiceAccount.toString().getBytes(StandardCharsets.UTF_8)
            );

            FirebaseOptions options = null;
            try {
                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                // Initialize service account credentials for manual JWT creation
                InputStream serviceAccountCopy = new ByteArrayInputStream(
                        firebaseServiceAccount.toString().getBytes(StandardCharsets.UTF_8)
                );
                getInstance().serviceAccountCredentials = ServiceAccountCredentials.fromStream(serviceAccountCopy);

                // Extract project_id and private_key_id from JSON
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(firebaseServiceAccount.toString(), JsonObject.class);
                getInstance().projectId = jsonObject.get("project_id").getAsString();
                getInstance().privateKeyId = jsonObject.get("private_key_id").getAsString();

            } catch (IOException e) {
                throw new RuntimeException("Exception Occurred while initializing Firebase: " + e.getMessage());
            }

            FirebaseApp.initializeApp(options);
        }
        LOGGER.info("Firebase Admin SDK Initialized Successfully");
    }

    /**
     * Initialize Firebase Admin SDK using InputStream
     *
     * @param serviceAccountStream InputStream containing service account JSON
     * @throws IOException if credentials are invalid
     */
    public static void initializeFromStream(InputStream serviceAccountStream) throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .build();

            FirebaseApp.initializeApp(options);
        }
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
     * Decode Token A (Firebase Custom Token) without verification
     * Extracts claims from the JWT payload
     *
     * @param tokenA The JWT token to decode
     * @return Map containing all claims from the token
     */
    public Map<String, Object> decodeTokenA(String tokenA) {
        try {
            // Split JWT into parts
            String[] parts = tokenA.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token format");
            }

            // Decode payload (second part)
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);

            // Parse JSON
            Gson gson = new Gson();
            @SuppressWarnings("unchecked")
            Map<String, Object> claims = gson.fromJson(payload, Map.class);

            LOGGER.info("Successfully decoded Token A. User ID: " + claims.get("uid"));
            return claims;

        } catch (Exception e) {
            LOGGER.error("Error decoding Token A: " + e.getMessage(), e);
            throw new RuntimeException("Failed to decode Token A", e);
        }
    }

    /**
     * Decode Token A using Firebase Admin SDK (with verification)
     * This verifies the token signature and validates it
     *
     * @param tokenA The Firebase ID token to verify and decode
     * @return FirebaseToken containing verified claims
     * @throws FirebaseAuthException if token verification fails
     */
    public FirebaseToken decodeAndVerifyTokenA(String tokenA) throws FirebaseAuthException {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(tokenA);
            LOGGER.info("Successfully verified and decoded Token A. User ID: " + decodedToken.getUid());
            return decodedToken;
        } catch (FirebaseAuthException e) {
            LOGGER.error("Token A verification failed: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Transform Token A to Token B
     * Decodes Token A, extracts claims, and creates a new Firebase ID Token (Token B)
     *
     * @param tokenA The source token to decode
     * @return New Firebase ID Token (Token B) with proper structure
     */
    public String transformTokenAToTokenB(String tokenA) {
        try {
            // Decode Token A (without verification for flexibility)
            Map<String, Object> tokenAClaims = decodeTokenA(tokenA);

            // Extract user details from Token A
            String userId = extractUserId(tokenAClaims);
            String email = extractEmail(tokenAClaims);
            String phoneNumber = extractPhoneNumber(tokenAClaims);
            Long authTime = extractAuthTime(tokenAClaims);

            // Build UserDetails for Token B
            UserDetails.Builder builder = new UserDetails.Builder()
                    .setUserId(userId)
                    .setSignInProvider("custom");

            if (email != null) {
                builder.setEmail(email);
            }
            if (phoneNumber != null) {
                builder.setPhoneNumber(phoneNumber);
            }
            if (authTime != null) {
                builder.setAuthTime(authTime);
            }

            UserDetails userDetails = builder.build();

            // Create Token B using the Firebase ID Token method
            String tokenB = createFirebaseIdToken(userDetails);

            LOGGER.info("Successfully transformed Token A to Token B for user: " + userId);
            return tokenB;

        } catch (Exception e) {
            LOGGER.error("Error transforming Token A to Token B: " + e.getMessage(), e);
            throw new RuntimeException("Failed to transform token", e);
        }
    }

    /**
     * Extract user ID from Token A claims
     */
    private String extractUserId(Map<String, Object> claims) {
        // Try different possible fields for user ID
        if (claims.containsKey("uid")) {
            return claims.get("uid").toString();
        } else if (claims.containsKey("user_id")) {
            return claims.get("user_id").toString();
        } else if (claims.containsKey("sub")) {
            return claims.get("sub").toString();
        }
        throw new IllegalArgumentException("No user ID found in Token A");
    }

    /**
     * Extract email from Token A claims
     */
    private String extractEmail(Map<String, Object> claims) {
        if (claims.containsKey("email")) {
            return claims.get("email").toString();
        }
        // Check nested claims object
        if (claims.containsKey("claims")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> nestedClaims = (Map<String, Object>) claims.get("claims");
            if (nestedClaims != null && nestedClaims.containsKey("email")) {
                return nestedClaims.get("email").toString();
            }
        }
        return null;
    }

    /**
     * Extract phone number from Token A claims
     */
    private String extractPhoneNumber(Map<String, Object> claims) {
        if (claims.containsKey("phone_number")) {
            return claims.get("phone_number").toString();
        }
        if (claims.containsKey("phoneNumber")) {
            return claims.get("phoneNumber").toString();
        }
        // Check nested claims object
        if (claims.containsKey("claims")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> nestedClaims = (Map<String, Object>) claims.get("claims");
            if (nestedClaims != null) {
                if (nestedClaims.containsKey("phoneNumber")) {
                    return nestedClaims.get("phoneNumber").toString();
                }
                if (nestedClaims.containsKey("phone_number")) {
                    return nestedClaims.get("phone_number").toString();
                }
            }
        }
        return null;
    }

    /**
     * Extract auth_time from Token A claims
     */
    private Long extractAuthTime(Map<String, Object> claims) {
        if (claims.containsKey("auth_time")) {
            Object authTime = claims.get("auth_time");
            if (authTime instanceof Number) {
                return ((Number) authTime).longValue();
            }
        }
        // Default to current time if not found
        return System.currentTimeMillis() / 1000;
    }

    /**
     * Create a Firebase ID Token with claims at root level
     * This creates a properly formatted Firebase ID token with RS256 signature
     *
     * @param userDetails User information to include in the token
     * @return JWT token string with Firebase ID token structure
     */
    public String createFirebaseIdToken(UserDetails userDetails) {
        if (serviceAccountCredentials == null) {
            throw new IllegalStateException("Firebase not initialized properly. Service account credentials missing.");
        }

        long nowSeconds = System.currentTimeMillis() / 1000;
        long expirationSeconds = nowSeconds + 3600; // 1 hour expiration

        PrivateKey privateKey = serviceAccountCredentials.getPrivateKey();

        // Build claims at root level
        Map<String, Object> claims = new HashMap<>();

        // Standard JWT claims
        claims.put("iss", "https://securetoken.google.com/" + projectId);
        claims.put("aud", projectId);
        claims.put("auth_time", userDetails.getAuthTime() != null ? userDetails.getAuthTime() : nowSeconds);
        claims.put("user_id", userDetails.getUserId());
        claims.put("sub", userDetails.getUserId());
        claims.put("iat", nowSeconds);
        claims.put("exp", expirationSeconds);

        // User profile claims at root level
        if (userDetails.getEmail() != null) {
            claims.put("email", userDetails.getEmail());
        }
        if (userDetails.getPhoneNumber() != null) {
            claims.put("phoneNumber", userDetails.getPhoneNumber());
        }

        // Firebase-specific claims at root level
        if (userDetails.getFirebaseIdentities() != null && !userDetails.getFirebaseIdentities().isEmpty()) {
            Map<String, Object> firebaseData = new HashMap<>();
            firebaseData.put("identities", userDetails.getFirebaseIdentities());

            if (userDetails.getSignInProvider() != null) {
                firebaseData.put("sign_in_provider", userDetails.getSignInProvider());
            }

            claims.put("firebase", firebaseData);
        } else {
            // Create default firebase claim with sign_in_provider
            Map<String, Object> firebaseData = new HashMap<>();
            firebaseData.put("identities", new HashMap<>());
            if (userDetails.getSignInProvider() != null) {
                firebaseData.put("sign_in_provider", userDetails.getSignInProvider());
            }
            claims.put("firebase", firebaseData);
        }

        // Add any additional custom claims at root level
        if (userDetails.getAdditionalClaims() != null) {
            claims.putAll(userDetails.getAdditionalClaims());
        }

        // Create JWT with proper headers (alg: RS256, kid: private_key_id, typ: JWT)
        return Jwts.builder()
                .setHeaderParam("kid", privateKeyId)
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    /**
     * Helper method to create UserDetails from JSONObject
     */
    public UserDetails getUserDetails(JSONObject userData) {
        UserDetails userDetails = new UserDetails.Builder()
                .setUserId(userData.getString("user_uid"))
                .setEmail(userData.getString("email"))
                .setAuthTime(System.currentTimeMillis() / 1000)
                .setSignInProvider("custom")
                .setPhoneNumber(userData.getString("phone_number"))
                .build();
        return userDetails;
    }

    /**
     * Example main method demonstrating Token A to Token B transformation
     */
    public static void main(String[] args) {
        try {
            FirebaseJWTManager jwtManager = FirebaseJWTManager.getInstance();

            // Token A (from your example)
            String tokenA = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJodHRwczovL2lkZW50aXR5dG9vbGtpdC5nb29nbGVhcGlzLmNvbS9nb29nbGUuaWRlbnRpdHkuaWRlbnRpdHl0b29sa2l0LnYxLklkZW50aXR5VG9vbGtpdCIsImlhdCI6MTc2NDU4Nzk5NywiZXhwIjoxNzY0NTkxNTk3LCJpc3MiOiJmaXJlYmFzZS1hZG1pbnNkay0xZmtwbUBodW5jaC1wcm9kLWUxMTMwLmlhbS5nc2VydmljZWFjY291bnQuY29tIiwic3ViIjoiZmlyZWJhc2UtYWRtaW5zZGstMWZrcG1AaHVuY2gtcHJvZC1lMTEzMC5pYW0uZ3NlcnZpY2VhY2NvdW50LmNvbSIsInVpZCI6IjFkMzE1ZWYxLTI1NzEtNDMxNC05NDU2LWIwMDQ1MjcwZGUxOSIsImNsYWltcyI6eyJlbWFpbCI6IjFkMzE1ZWYxLTI1NzEtNDMxNC05NDU2LWIwMDQ1MjcwZGUxOUBodW5jaG1vYmlsZS5jb20iLCJwaG9uZU51bWJlciI6Iis5MTkzMTEzODQ5MTUifX0.k2CEVbVuHGMGFczKZD_RKEkTkeXfd935PKGQEpBhBJzwf1wVZ-_yhlANjDH24bszRePB-2UADeqS7VzfycPSWCPIzaQ8YT9dK7VRqRf0MUQgRxQ0Ql6EMfTXuOAiQ4iO6qIAH53FZd7Fw0tEsQFQVeTnnwTeSsXUq-qPHYdVqd8RtZNzqFLflurpzIE_Cy7j6Hi_aAROyLRJinznEjxFQKU7LCJae3Ib0hexU8-bK4Ju7rInYycnifzwkjXiKnwrOeCds-wgYMK7z3cXB8w8AFhKrDiQjI6MIbWEj6heHnKLRe6zAifP0g1txPbm9g7Lmu6kx_PkXOjdokNMUXuxzA";

            System.out.println("=== Decoding Token A ===");
            Map<String, Object> decodedClaims = jwtManager.decodeTokenA(tokenA);
            System.out.println("Token A Claims: " + decodedClaims);
            System.out.println();

            System.out.println("=== Transforming Token A to Token B ===");
            String tokenB = jwtManager.transformTokenAToTokenB(tokenA);
            System.out.println("Token B (Firebase ID Token):");
            System.out.println(tokenB);
            System.out.println();

            // Decode Token B to verify structure
            System.out.println("=== Token B Structure ===");
            String[] parts = tokenB.split("\\.");
            String header = new String(Base64.getUrlDecoder().decode(parts[0]));
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));

            System.out.println("Header: " + header);
            System.out.println("Payload: " + payload);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Builder class for user details
     */
    @Getter
    public static class UserDetails {
        private String userId;
        private String email;
        private Long authTime;
        private String signInProvider;
        private String phoneNumber;
        private Map<String, Object> firebaseIdentities;
        private Map<String, Object> additionalClaims;

        private UserDetails() {
            this.firebaseIdentities = new HashMap<>();
            this.additionalClaims = new HashMap<>();
        }

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

            public Builder setAuthTime(Long authTime) {
                userDetails.authTime = authTime;
                return this;
            }

            public Builder setSignInProvider(String signInProvider) {
                userDetails.signInProvider = signInProvider;
                return this;
            }

            public Builder addIdentity(String provider, List<String> identifiers) {
                userDetails.firebaseIdentities.put(provider, identifiers);
                return this;
            }

            public Builder addCustomClaim(String key, Object value) {
                userDetails.additionalClaims.put(key, value);
                return this;
            }

            public Builder setPhoneNumber(String phoneNumber) {
                userDetails.phoneNumber = phoneNumber;
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
}