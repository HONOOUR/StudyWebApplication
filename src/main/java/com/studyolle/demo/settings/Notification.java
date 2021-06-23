package com.studyolle.demo.settings;

import com.studyolle.demo.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Notification {
    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb;
}
