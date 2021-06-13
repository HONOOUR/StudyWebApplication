package com.studyolle.demo.settings;

import com.studyolle.demo.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor //or default constructor
public class Profile {

    private String bio;

    private String url;

    private String occupation;

    private String location; // varchar(255)

    public Profile(Account account) {
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
    }
}
