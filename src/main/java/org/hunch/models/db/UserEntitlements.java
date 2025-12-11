package org.hunch.models.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hunch.utils.Common;
import org.json.JSONObject;

@Data
public class UserEntitlements {

    @JsonProperty("user_uid")
    private String userUid;

    @JsonProperty("__replicated_at")
    private String replicatedAt;

    @JsonProperty("__replicated_from")
    private String replicatedFrom;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("expire_date")
    private String expireDate;

    @JsonProperty("grace_period_expires_date")
    private String gracePeriodExpiresDate;

    @JsonProperty("is_cancelled")
    private Boolean isCancelled = false;

    @JsonProperty("presented_offering_id")
    private String presentedOfferingId;

    @JsonProperty("product_identifier")
    private String productIdentifier;

    @JsonProperty("product_plan_identifier")
    private String productPlanIdentifier;

    @JsonProperty("purchase_date")
    private String purchaseDate;

    @JsonProperty("subscription_plan")
    private String subscriptionPlan;

    @JsonProperty("purchase_source")
    private String purchaseSource;

    @JsonProperty("period_type")
    private String periodType;

    /**
     * Default constructor
     */
    public UserEntitlements() {
        this.replicatedFrom = "app";
        this.isCancelled = false;
    }

    /**
     * Constructor for basic user entitlements
     * @param userUid User unique identifier
     * @param presentedOfferingId Presented offering ID
     * @param productIdentifier Product identifier
     * @param subscriptionPlan Subscription plan
     * @param periodType Period type (e.g., "NORMAL")
     * @param durationInDays Duration in days for expiration
     */
    public UserEntitlements(String userUid, String presentedOfferingId, String productIdentifier,
                           String subscriptionPlan, String periodType, int durationInDays) {
        this.userUid = userUid;
        this.presentedOfferingId = presentedOfferingId;
        this.productIdentifier = productIdentifier;
        this.subscriptionPlan = subscriptionPlan;
        this.periodType = periodType;
        this.purchaseSource = "app";
        this.replicatedFrom = "app";
        this.isCancelled = false;

        String currentTimestamp = Common.getCurrentTimestamp();
        this.createdAt = currentTimestamp;
        this.updatedAt = currentTimestamp;
        this.replicatedAt = currentTimestamp;
        this.purchaseDate = currentTimestamp;

        // Calculate expire date
        this.expireDate = Common.getFutureTimestamp(durationInDays);
    }

    /**
     * Set expiration date based on days from current timestamp
     * @param daysFromNow Number of days from current timestamp
     */
    public void setExpireDateFromDays(int daysFromNow) {
        this.expireDate = Common.getFutureTimestamp(daysFromNow);
    }

    /**
     * Set grace period expiration date based on days from current timestamp
     * @param daysFromNow Number of days from current timestamp
     */
    public void setGracePeriodExpiresDateFromDays(int daysFromNow) {
        this.gracePeriodExpiresDate = Common.getFutureTimestamp(daysFromNow);
    }

    /**
     * Initialize timestamps to current time
     */
    public void initializeTimestamps() {
        String currentTimestamp = Common.getCurrentTimestamp();
        this.createdAt = currentTimestamp;
        this.updatedAt = currentTimestamp;
        this.replicatedAt = currentTimestamp;
        this.purchaseDate = currentTimestamp;
    }

    @Override
    public String toString() {
        return Common.mapper.writeValueAsString(this);
    }

    public JSONObject toJsonObject() {
        return new JSONObject(this.toString());
    }
}
