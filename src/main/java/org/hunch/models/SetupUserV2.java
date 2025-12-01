package org.hunch.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javafaker.Faker;
import lombok.Data;
import org.hunch.enums.DesiredRelationshipType;
import org.hunch.enums.Ethnicity;
import org.hunch.enums.Gender;
import org.hunch.enums.Tags;
import org.hunch.utils.Common;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SetupUserV2 {
    private User user;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class User {
        private String adjustAdid;
        private boolean becomeCreator;
        private String bio;
        private String blurredDp;
        private String collegename;
        private ArrayList<String> datingPreference;
        private ArrayList<String> desiredRelationshipType;
        private String dob;
        private String dp;
        private String ethnicity;
        private String ethnicityVisibility;
        private String firstName;
        private String gender;
        private boolean genderVisibility;
        private String guestUserEmail;
        private Height height;
        private ArrayList<String> interests;
        @JsonProperty("isDeleted") private boolean isDeleted;
        @JsonProperty("isFtueOnboarded")private boolean isFtueOnboarded;
        @JsonProperty("isFtueOnboardingCompleted")private boolean isFtueOnboardingCompleted;
        @JsonProperty("isPhotoUpgradeScreenRequired")private boolean isPhotoUpgradeScreenRequired;
        @JsonProperty("isProfileSetupStatus")private String isProfileSetupStatus;
        private String lastName;
        private String location;
        private String mimetype;
        private ArrayList<String> multipleDps;
        private String name;
        private boolean nsfwSetting;
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
        private boolean zodiacSignVisibility;
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
        private boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Workplace {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class DietaryPreference {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private boolean visibility;
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
        private boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class DrugUsage {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class EducationLevel {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Exercise {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class FamilyPlans {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private boolean visibility;
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
        private boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Languages {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Occupation {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class PetPreference {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Politics {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private boolean visibility;
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
        private boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class SmokingHabits {
        private String type;
        private String value;
        private ArrayList<String> valueArray;
        private boolean visibility;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Tag {
        private String icon;
        private String parent;
        private String tagName;
    }

    public  SetupUserV2 setRandomData(){
        Faker fake = new Faker();
        this.user = new User();
        this.user.height = new Height();
        user.setFirstName(fake.name().firstName());
        user.setDob(fake.date().birthday(18, 30).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString());
        user.setGender(Common.randomEnum(Gender.class).getString());
        user.setDatingPreference(new ArrayList<>(Collections.singleton(Common.randomEnum(Gender.class).getString())));
        user.setDesiredRelationshipType(new ArrayList<>(Collections.singleton(Common.randomEnum(DesiredRelationshipType.class).getString())));
        user.height.setFeet(fake.number().numberBetween(4, 6));
        user.height.setInches(fake.number().numberBetween(5,11));
        user.setEthnicity(Common.randomEnum(Ethnicity.class).getDisplayName());
        setTags();
        user.setZodiacSignVisibility(true);
        user.setNsfwSetting(true);

        return this;
    }

    private void setTags(){
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
    
}
