package org.hunch.models.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hunch.utils.Common;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@Data
public class InsertUserFeatureUsage {

    @JsonProperty("user_uid")
    private String userUid;

    @JsonProperty("feature_type")
    private String featureType;

    @JsonProperty("start_timestamp")
    private String startTimestamp;

    @JsonProperty("end_timestamp")
    private String endTimestamp;

    @JsonProperty("last_used_at")
    private String lastUsedAt;

    @JsonProperty("last_purchased_at")
    private String lastPurchasedAt;

    @JsonProperty("total_credits")
    private Integer totalCredits = 0;

    @JsonProperty("attributes")
    private Map<String, Object> attributes = new HashMap<>();

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("__replicated_at")
    private String replicatedAt;

    @JsonProperty("__replicated_from")
    private String replicatedFrom;

    @JsonProperty("phone_identifier_hash")
    private String phoneIdentifierHash;

    @JsonProperty("email_identifier_hash")
    private String emailIdentifierHash;

    @JsonProperty("device_identifier_hash")
    private String deviceIdentifierHash;

    @JsonProperty("credits")
    private Map<String, Object> credits = new HashMap<>();

    /**
     * Default constructor
     */
    public InsertUserFeatureUsage() {
        this.replicatedFrom = "app";
    }

    /**
     * Constructor for basic user feature usage
     * @param userUid User unique identifier
     * @param featureType Type of feature (e.g., "crush")
     * @param totalCredits Total credits for the feature
     */
    public InsertUserFeatureUsage(String userUid, String featureType, Integer totalCredits) {
        this.userUid = userUid;
        this.featureType = featureType;
        this.totalCredits = totalCredits;
        this.replicatedFrom = "app";
        this.createdAt = Common.getCurrentTimestamp();
        this.updatedAt = Common.getCurrentTimestamp();
        this.replicatedAt = Common.getCurrentTimestamp();
        setLastRefillAt(Common.getCurrentTimestamp());
    }

    /**
     * Set attributes from JSONObject
     * @param attributesJson JSONObject containing attributes data
     */
    public void setAttributesFromJson(JSONObject attributesJson) {
        if (attributesJson != null) {
            this.attributes = attributesJson.toMap();
        }
    }

    /**
     * Set credits from JSONObject
     * @param creditsJson JSONObject containing credits data
     */
    public void setCreditsFromJson(JSONObject creditsJson) {
        if (creditsJson != null) {
            this.credits = creditsJson.toMap();
        }
    }

    /**
     * Initialize default credits structure
     * @param freeCredits Free credits count
     * @param referralCredits Referral credits count
     * @param hunchPlusCredits Hunch Plus credits count
     * @param directPurchaseCredits Direct purchase credits count
     */
    public void initializeCredits(int freeCredits, int referralCredits, int hunchPlusCredits, int directPurchaseCredits) {
        this.credits = new HashMap<>();
        this.credits.put("free", freeCredits);
        this.credits.put("referral", referralCredits);
        this.credits.put("hunch_plus", hunchPlusCredits);
        this.credits.put("direct_purchase", directPurchaseCredits);
    }

    /**
     * Set last refill timestamp in attributes
     * @param lastRefillAt ISO 8601 timestamp string
     */
    public void setLastRefillAt(String lastRefillAt) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.put("lastRefillAt", lastRefillAt);
    }

    @Override
    public String toString() {
        return Common.mapper.writeValueAsString(this);
    }

    public JSONObject toJsonObject() {
        return new JSONObject(this.toString());
    }
}
