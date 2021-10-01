package com.studyolle.demo.modules.study;

import com.studyolle.demo.modules.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyFactory {
    @Autowired private StudyService studyService;

    public Study createStudy(String path, Account account) {
        Study study = new Study();
        study.setPath(path);
        studyService.createNewStudy(study, account);
        return study;
    }
}
