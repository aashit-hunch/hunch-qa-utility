package org.hunch.models.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javafaker.Faker;
import lombok.Data;
import org.hunch.utils.Common;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@Data
public class InsertUsers {
    @JsonProperty("user_uid") private String userUid;
    @JsonProperty("email") private String email;
    @JsonProperty("username") private String username;
    @JsonProperty("restricted_username") private String restrictedUsername;
    @JsonProperty("name") private String name;
    @JsonProperty("bio") private String bio;
    @JsonProperty("dob") private String dob;
    @JsonProperty("age") private Integer age=0;
    @JsonProperty("dp") private String dp;
    @JsonProperty("gender") private String gender;
    @JsonProperty("interests") private java.util.Map<String,Object> interests=new HashMap<>();
    @JsonProperty("location") private String location="";
    @JsonProperty("source") private String source;
    @JsonProperty("open_id") private String openId;
    @JsonProperty("become_creator") private Boolean becomeCreator=false;
    @JsonProperty("creator_access_ts") private String creatorAccessTs;
    @JsonProperty("elite_access_ts") private String eliteAccessTs;
    @JsonProperty("status") private String status;
    @JsonProperty("is_private") private Boolean isPrivate=false;
    @JsonProperty("country") private String country;
    @JsonProperty("city") private String city;
    @JsonProperty("region") private String region;
    @JsonProperty("collegename") private String collegename="";
    @JsonProperty("is_deleted") private Boolean isDeleted=false;
    @JsonProperty("pronouns") private String pronouns="";
    @JsonProperty("twitter_user_id") private String twitterUserId="";
    @JsonProperty("twitter_screen_name") private String twitterScreenName="";
    @JsonProperty("suspended_till") private String suspendedTill;
    @JsonProperty("phone_number") private String phoneNumber;
    @JsonProperty("first_name") private String firstName;
    @JsonProperty("last_name") private String lastName;
    @JsonProperty("circle_dp") private String circleDp;
    @JsonProperty("nsfw_setting") private Boolean nsfwSetting;
    @JsonProperty("last_logged_in") private String lastLoggedIn;
    @JsonProperty("adjust_adid") private String adjustAdId;
    @JsonProperty("height") private Map<String,Object> height=new HashMap<>();
    @JsonProperty("dating_preference") private Map<String,Object> datingPreference=new HashMap<>();
    @JsonProperty("desired_relationship_type") private Map<String,Object> desiredRelationshipType=new HashMap<>();
    @JsonProperty("is_profile_setup_status") private String isProfileSetupStatus;
    @JsonProperty("latitude") private float latitude=0;
    @JsonProperty("longitude") private float longitude=0;
    @JsonProperty("created_at") private String createdAt;
    @JsonProperty("updated_at") private String updatedAt;
    @JsonProperty("is_ftue_onboarded") private Boolean isFtueOnboarded=false;
    @JsonProperty("is_ftue_onboarding_completed") private Boolean isFtueOnboardingCompleted=false;
    @JsonProperty("is_interest_feed_added") private Boolean isInterestFeedAdded=false;
    @JsonProperty("is_circle_onboarded") private Boolean isCircleOnboarded=false;
    @JsonProperty("zodiac_sign") private String zodiacSign="";
    @JsonProperty("generated_dp") private String generatedDp;
    @JsonProperty("verticle_image_ai") private String verticalImageAi="";
    @JsonProperty("shareable_url") private String shareableUrl="";
    @JsonProperty("ai_image_palette") private Map<String,Object> aiImagePalette = new HashMap<>();
    @JsonProperty("__replicated_at") private String replicatedAt;
    @JsonProperty("__replicated_from") private String replicatedFrom;
    @JsonProperty("is_user_first_comment_created_at") private Boolean isUserFirstCommentCreatedAt;
    @JsonProperty("reveal_params") private Map<String,Object> revealParams=new HashMap<>();
    @JsonProperty("is_user_bot_followed") private Boolean isUserBotFollowed=false;
    @JsonProperty("is_me_bot_initiated") private Boolean isMeBotInitiated=false;
    @JsonProperty("bot_likes_count") private Integer botLikesCount=0;
    @JsonProperty("__updated_at") private String updatedSyncAt;
    @JsonProperty("me_bot_initiated_at") private String meBotInitiatedAt;
    @JsonProperty("tags") private JSONArray tags;
    @JsonProperty("is_user_first_comment") private Boolean isUserFirstComment=false;
    @JsonProperty("mimetype") private String mimeType;
    @JsonProperty("deletion_status") private String deletionStatus;
    @JsonProperty("device") private String device;
    @JsonProperty("deleted_at") private String deletedAt;
    @JsonProperty("last_wave_read_at") private String lastWaveReadAt;
    @JsonProperty("verification_status") private String verificationStatus="";
    @JsonProperty("multiple_dps") private Map<String,Object> multipleDps=new HashMap<>();
    @JsonProperty("location_point") private String locationPoint;
    @JsonProperty("install_status") private String installStatus;
    @JsonProperty("uninstall_at") private String uninstallAt;
    @JsonProperty("is_ai_chat_required") private Boolean isAiChatRequired=false;
    @JsonProperty("otp_active_till") private String otpActiveTill;
    @JsonProperty("otp_hash") private String otpHash;
    @JsonProperty("iv") private String iv;
    @JsonProperty("blurred_dp") private String blurredDp;
    @JsonProperty("user_groups") private Map<String,Object> userGroups;
    @JsonProperty("is_photo_upgrade_screen_required") private Boolean isPhotoUpgradeScreenRequired=false;
    @JsonProperty("profile_attributes") private Map<String,Object> profileAttributes=new HashMap<>();
    @JsonProperty("sub_gender") private String subGender;
    @JsonProperty("gender_visibility") private boolean genderVisibility;
    @JsonProperty("ethnicity") private String ethnicity;
    @JsonProperty("ethnicity_visibility") private boolean ethnicityVisibility;
    @JsonProperty("benefits") private String benefits;
    @JsonProperty("agent_id") private String agentId;
    @JsonProperty("vibe_tribe_shareable_image_url") private String vibeTribeShareableImageUrl="";
    @JsonProperty("address_components") private Map<String,Object> addressComponents;
    @JsonProperty("geo_source") private String geoSource="";
    @JsonProperty("zodiac_sign_visibility") private boolean zodiacSignVisibility;
    @JsonProperty("user_meta") private Map<String,Object> userMeta;
    @JsonProperty("liveness_reference_image") private Map<String,Object> livenessReferenceImage=new HashMap<>() ;
    @JsonProperty("initial_location_details") private String initialLocationDetails;
    @JsonProperty("profile_setup_at") private String profileSetupAt;
    @JsonProperty("referral_link") private String referralLink="";
    @JsonProperty("referred_by_user_uid") private String referredByUserUid;
    @JsonProperty("referral_status") private String referralStatus;
    @JsonProperty("referral_completed_at") private String referralCompletedAt;
    @JsonProperty("referral_code") private String referralCode;
    @JsonProperty("guest_user_email") private String guestUserEmail;
    @JsonProperty("free_trial_impression") private Integer freeTrialImpression=0;
    @JsonProperty("mbti_type") private String mbtiType;
    @JsonProperty("guest_user_id") private String guestUserId;
    @JsonProperty("subscription_id") private String subscriptionId;
    @JsonProperty("utm") private String utm;
    @JsonProperty("generated_ethnicity") private String generatedEthnicity;
    @JsonProperty("signup_app_version") private String signupAppVersion;
    @JsonProperty("duoplus_adb") private String duoPlusAdb;
    @JsonProperty("duoplus_device_id") private String duoPlusDeviceId;
    @JsonProperty("app_version") private String appVersion;
    @JsonProperty("is_liquidity_toggle_eligible") private Boolean isLiquidityToggleEligible;
    @JsonProperty("signup_location_details") private Map<String,Object> signupLocationDetails;


    public InsertUsers(String userId,String username,String phoneNumber,String email,String device,String appVersion,String refCode,String adId){
        this.status="pending";
        this.source="custom_phone_number";
        this.username=username;
        this.firstName= Faker.instance().name().firstName();
        this.lastName="GenData";
        this.phoneNumber=phoneNumber;
        this.nsfwSetting=true;
        this.adjustAdId=adId;
        this.email=email;
        this.isProfileSetupStatus="initiated";
        this.replicatedFrom="app";
        this.device=device;
        this.userUid=userId;
        this.genderVisibility=true;
        this.ethnicityVisibility=true;
        this.zodiacSignVisibility=true;
        this.signupAppVersion=appVersion;
        this.appVersion=appVersion;
        this.referralCode=refCode;
        this.createdAt= Common.getCurrentTimestamp();
        this.updatedAt= Common.getCurrentTimestamp();
        this.restrictedUsername="";
        this.name="";
        this.bio="";
        this.dp="";
        this.replicatedAt= Common.getCurrentTimestamp();
    }
    public void insertSignupLocationDetails(String ipCity, String ipRegion, String ipCountry) {
        if (this.signupLocationDetails == null) {
            this.signupLocationDetails = new HashMap<>();
        }
        this.city=ipCity;
        this.region=ipRegion;
        this.country=ipCountry;
        this.signupLocationDetails.put("ipCity", ipCity);
        this.signupLocationDetails.put("ipRegion", ipRegion);
        this.signupLocationDetails.put("ipCountry", ipCountry);

    }
    @Override
    public String toString(){
        return Common.mapper.writeValueAsString(this);
    }

    public JSONObject toJsonObject(){

        IO.println("JSON String : "+ this.toString());
        return new JSONObject(this.toString());
    }
}
