package org.hunch.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javafaker.Faker;
import lombok.Data;
import org.hunch.constants.GlobalData;
import org.hunch.enums.DesiredRelationshipType;
import org.hunch.enums.Ethnicity;
import org.hunch.enums.Gender;
import org.hunch.enums.Tags;
import org.hunch.utils.Common;
import org.hunch.utils.ThreadUtils;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SetupUserV2 {
    private User user;
    @JsonIgnore Faker fake = new Faker();
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class User {
        private String adjustAdid;
        private Boolean becomeCreator;
        private String bio;
        private String blurredDp;
        private String collegename;
        private List<String> datingPreference;
        private List<String> desiredRelationshipType;
        private String dob;
        private String dp;
        private String ethnicity;
        private String ethnicityVisibility;
        private String firstName;
        private String gender;
        private Boolean genderVisibility;
        private String guestUserEmail;
        private Height height;
        private ArrayList<String> interests;
        @JsonProperty("isDeleted") private Boolean isDeleted;
        @JsonProperty("isFtueOnboarded")private Boolean isFtueOnboarded;
        @JsonProperty("isFtueOnboardingCompleted")private Boolean isFtueOnboardingCompleted;
        @JsonProperty("isPhotoUpgradeScreenRequired")private Boolean isPhotoUpgradeScreenRequired;
        @JsonProperty("isProfileSetupStatus")private String isProfileSetupStatus;
        private String lastName;
        private String location;
        private String mimetype;
        private ArrayList<String> multipleDps;
        private String name;
        private Boolean nsfwSetting;
        private String openId;
        private String phoneNumber;
        private ProfileAttributes profileAttributes;
        private String pronouns;
        private QuizPayload quizPayload;
        private String shareableUrl;
        private String source;
        private String subGender;
        private ArrayList<Tag> tags;
        private String transactionId;
        private UserDevices userDevices;
        private String username;
        private Utm utm;
        private String verificationStatus;
        private String zodiacSign;
        private Boolean zodiacSignVisibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class UserDevices {
        private String googleAdId;
        private String idfa;
        private String idfv;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Utm {
        private String utm_campaign;
        private String utm_content;
        private String utm_medium;
        private String utm_source;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class WeedUsage {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private Boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Workplace {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private Boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class DietaryPreference {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private Boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class DimensionScores {
        private int extraversion;
        private int feeling;
        private int introversion;
        private int intuition;
        private int judging;
        private int perceiving;
        private int sensing;
        private int thinking;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class DrinkingHabits {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private Boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class DrugUsage {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private Boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class EducationLevel {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private Boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Exercise {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private Boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class FamilyPlans {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private Boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Height {
        private int feet;
        private int inches;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class HomeTown {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private Boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Languages {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private Boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Occupation {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private Boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class PetPreference {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private Boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Politics {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private Boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class ProfileAttributes {
        private DietaryPreference dietaryPreference;
        private DrinkingHabits drinkingHabits;
        private DrugUsage drugUsage;
        private EducationLevel educationLevel;
        private Exercise exercise;
        private FamilyPlans familyPlans;
        private HomeTown homeTown;
        private Languages languages;
        private Occupation occupation;
        private PetPreference petPreference;
        private Politics politics;
        private Religion religion;
        private SmokingHabits smokingHabits;
        private WeedUsage weedUsage;
        private Workplace workplace;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class QuizAttempt {
        private String attribute;
        private String option_id;
        private String poll_id;
        private int value;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class QuizPayload {
        private String campaignId;
        private int completedAt;
        private DimensionScores dimensionScores;
        private String personalityType;
        private ArrayList<QuizAttempt> quizAttempts;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Religion {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private Boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class SmokingHabits {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private Boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Tag {
        private String icon;
        private String parent;
        private String tagName;
    }

    public SetupUserV2() {
        this.user = new User();
        this.user.setZodiacSignVisibility(true);
        this.user.setNsfwSetting(true);
    }
    /**
     * Sets random data for user profile setup
     * @return
     */
    public  SetupUserV2 setRandomData(){

        this.user = new User();
        setFirstName();
        setDob();
        setGender();
        setDatingPreference();
        setDesiredRelationshipType();
        setEthinicity();
        setHeight();
        setTags();
        return this;
    }

    /**
     * Sets random tags for the user profile
     */
    public void setTags(){
        ArrayList<Tag> tags = new ArrayList<>();
        List<Tags> tagsEnum = Common.getRandomEnumList(Tags.class,10);
        for(int i=0;i<tagsEnum.size();i++){
            Tag tag = new Tag();
            tag.setTagName(tagsEnum.get(i).getDisplayName());
            tag.setParent(tagsEnum.get(i).getParentCategory());
            tag.setIcon(tagsEnum.get(i).getIcon());
            tags.add(tag);
        }
        this.user.setTags(tags);
    }

    /**
     * Final setup call to mark profile setup as completed
     * @return
     */
    public SetupUserV2 setFinalData(){
        this.user = new User();
        this.user.setIsProfileSetupStatus("completed");
        this.user.setZodiacSignVisibility(true);
        this.user.setNsfwSetting(true);
        this.user.setGenderVisibility(true);
        this.user.setIsPhotoUpgradeScreenRequired(false);
        return this;
    }

    public SetupUserV2 setDob(int... dob){
        if(dob.length>0){
            this.user.setDob(fake.date().past(dob[0]*365, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString());
            return this;
        }
        else {
            this.user.setDob(fake.date().birthday(18, 30).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString());
        }
        return this;
    }

    public SetupUserV2 setFirstName(String... firstName){
        if(firstName.length>0){
            this.user.setFirstName(firstName[0]);
            return this;
        }
        this.user.setFirstName(fake.name().firstName());
        return this;
    }

    public SetupUserV2 setGender(String... gender) {
        if (gender.length > 0) {
            ThreadUtils.userDto.get().setGender(Gender.fromString(gender[0]));
        } else if (GlobalData.USER_PROVIDED_DATA) {
            ThreadUtils.userDto.get().setGender(GlobalData.GENDER_TYPE);
        } else {
            ThreadUtils.userDto.get().setGender(Common.randomEnum(Gender.class));
        }
        this.user.setGender(ThreadUtils.userDto.get().getGender().getString());
        return this;
    }

    public SetupUserV2 setDatingPreference(String... datingPreference) {
        if (datingPreference.length > 0) {
            ThreadUtils.userDto.get().setDating_preferences(new ArrayList<>(Collections.singleton(Gender.fromString(datingPreference[0]))));
        } else if (GlobalData.USER_PROVIDED_DATA) {
            ThreadUtils.userDto.get().setDating_preferences(new ArrayList<>(Collections.singleton(GlobalData.GENDER_TYPE_PREFERENCE)));
        } else {
            ThreadUtils.userDto.get().setDating_preferences(new ArrayList<>(Collections.singleton(Common.randomEnum(Gender.class))));
        }
        this.user.setDatingPreference(ThreadUtils.userDto.get().getDating_preferences().stream()
                .map(Gender::getString)
                .collect(java.util.stream.Collectors.toList()));
        return this;
    }

    public SetupUserV2 setDesiredRelationshipType(String... desiredRelationshipType) {
        if (desiredRelationshipType.length > 0) {
            ThreadUtils.userDto.get().setDesired_relationship_types(new ArrayList<>(Collections.singleton(DesiredRelationshipType.fromString(desiredRelationshipType[0]))));
        }else {
            ThreadUtils.userDto.get().setDesired_relationship_types(new ArrayList<>(Collections.singleton(Common.randomEnum(DesiredRelationshipType.class))));
        }
        this.user.setDesiredRelationshipType(ThreadUtils.userDto.get().getDesired_relationship_types().stream()
                .map(DesiredRelationshipType::getString)
                .collect(java.util.stream.Collectors.toList()));
        return this;
    }

    public SetupUserV2 setEthinicity(String... Ethinicity) {
        if (Ethinicity.length > 0) {

            ThreadUtils.userDto.get().setEthnicity(Ethnicity.fromString(Ethinicity[0]));
        } else {
            ThreadUtils.userDto.get().setEthnicity(Common.randomEnum(Ethnicity.class));
        }
        this.user.ethnicity= ThreadUtils.userDto.get().getEthnicity().getDisplayName();

        return this;
    }

    public SetupUserV2 setHeight(int... height){
        this.user.height = new Height();
        if(height.length==2){
            this.user.height.setFeet(height[0]);
            this.user.height.setInches(height[1]);
            return this;
        }
        this.user.height.setFeet(fake.number().numberBetween(4, 6));
        this.user.height.setInches(fake.number().numberBetween(5,11));
        return this;
    }


}
