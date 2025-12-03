package org.hunch.dto;

import lombok.Data;
import org.hunch.enums.DesiredRelationshipType;
import org.hunch.enums.Ethnicity;
import org.hunch.enums.Gender;
import org.hunch.enums.Tags;

import java.util.List;

@Data
public class UserDetailsDTO {
    private String user_id;
    private String phone_number;
    private String email;
    private Gender gender;
    private List<Gender> dating_preferences;
    private Ethnicity ethnicity;
    List<DesiredRelationshipType> desired_relationship_types;
    List<Tags> tags;
    String mainDpUrl;
    List<String> otherDpUrls;
}
