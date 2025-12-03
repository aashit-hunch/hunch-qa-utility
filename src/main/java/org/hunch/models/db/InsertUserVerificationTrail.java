package org.hunch.models.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hunch.utils.Common;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@Data
public class InsertUserVerificationTrail {

    @JsonProperty("user_uid")
    private String userUid;

    @JsonProperty("face_match")
    private Map<String, Object> faceMatch = new HashMap<>();

    @JsonProperty("liveness")
    private Map<String, Object> liveness = new HashMap<>();

    @JsonProperty("analysis")
    private Map<String, Object> analysis = new HashMap<>();

    @JsonProperty("fail_code")
    private String failCode;

    @JsonProperty("liveness_analysis")
    private Map<String, Object> livenessAnalysis = new HashMap<>();

    @JsonProperty("action")
    private String action;

    @JsonProperty("matched_with")
    private String matchedWith;

    @JsonProperty("status")
    private String status;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("__replicated_at")
    private String replicatedAt;

    @JsonProperty("__replicated_from")
    private String replicatedFrom;

    @JsonProperty("scammer_face_detection_score")
    private Double scammerFaceDetectionScore = 0.0;

    @JsonProperty("scammer_face_match_external_id")
    private String scammerFaceMatchExternalId;

    @JsonProperty("scammer_face_match_s3_path")
    private String scammerFaceMatchS3Path;

    @JsonProperty("scammer_face_search_result")
    private Map<String, Object> scammerFaceSearchResult = new HashMap<>();

    /**
     * Constructor for basic user verification trail
     * @param userUid User unique identifier
     * @param action Action type (e.g., "onboard")
     * @param dp URL of matched profile picture
     * @param status Verification status (e.g., "verified")
     */
    public InsertUserVerificationTrail(String userUid, String action, String dp, String status) {
        this.userUid = userUid;
        this.action = action;
        this.matchedWith = dp;
        this.status = status;
        this.replicatedFrom = "app";
        this.createdAt = Common.getCurrentTimestamp();
        this.updatedAt = Common.getCurrentTimestamp();
        this.replicatedAt = Common.getCurrentTimestamp();
        this.scammerFaceDetectionScore = 0.0;

    }

    /**
     * Default constructor
     */
    public InsertUserVerificationTrail() {
        this.replicatedFrom = "app";
        this.scammerFaceDetectionScore = 0.0;
    }

    /**
     * Set face match data from JSONObject
     * @param faceMatchJson JSONObject containing face match data
     */
    public void setFaceMatchFromJson(JSONObject faceMatchJson) {
        if (faceMatchJson != null) {
            this.faceMatch = faceMatchJson.toMap();
        }
    }

    /**
     * Set liveness data from JSONObject
     * @param livenessJson JSONObject containing liveness data
     */
    public void setLivenessFromJson(JSONObject livenessJson) {
        if (livenessJson != null) {
            this.liveness = livenessJson.toMap();
        }
    }

    /**
     * Set analysis data from JSONObject
     * @param analysisJson JSONObject containing analysis data
     */
    public void setAnalysisFromJson(JSONObject analysisJson) {
        if (analysisJson != null) {
            this.analysis = analysisJson.toMap();
        }
    }

    /**
     * Set liveness analysis data from JSONObject
     * @param livenessAnalysisJson JSONObject containing liveness analysis data
     */
    public void setLivenessAnalysisFromJson(JSONObject livenessAnalysisJson) {
        if (livenessAnalysisJson != null) {
            this.livenessAnalysis = livenessAnalysisJson.toMap();
        }
    }

    /**
     * Set scammer face search result from JSONObject
     * @param scammerFaceSearchResultJson JSONObject containing scammer face search result
     */
    public void setScammerFaceSearchResultFromJson(JSONObject scammerFaceSearchResultJson) {
        if (scammerFaceSearchResultJson != null) {
            this.scammerFaceSearchResult = scammerFaceSearchResultJson.toMap();
        }
    }

    @Override
    public String toString() {
        return Common.mapper.writeValueAsString(this);
    }

    public JSONObject toJsonObject() {
        return new JSONObject(this.toString());
    }
}
