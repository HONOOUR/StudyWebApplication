package com.studyolle.demo.settings;

import com.studyolle.demo.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
public class Profile {

    @Length(max=35)
    private String bio;

    @Length(max=50)
    private String url;

    @Length(max=50)
    private String occupation;

    @Length(max=50)
    private String location; // varchar(255)

    private String profileImage;
}
