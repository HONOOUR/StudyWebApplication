package com.studyolle.demo.modules.study;

import com.studyolle.demo.modules.account.UserAccount;
import com.studyolle.demo.domain.Account;
import com.studyolle.demo.domain.Study;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StudyTest {

    private Study study;
    private Account account;
    private UserAccount userAccount;

    @BeforeEach
    void beforeEach() {
        study = new Study();
        account = new Account();
        account.setNickname("jieun");
        account.setPassword("12341234");
        userAccount = new UserAccount(account);
    }

    @DisplayName("스터디 공개중, 모집중, 스터디 멤버 또는 관리자 중에 없을 때 가입 가능")
    @Test
    void isJoinable() {
        study.setPublished(true);
        study.setRecruiting(true);

        assertTrue(study.isJoinable(userAccount));
    }

    @DisplayName("스터디 공개중, 모집중, 스터디 관리자면 가입 불가능")
    @Test
    void isJoinable_false_for_manager() {
        study.setPublished(true);
        study.setRecruiting(true);
        study.addManager(account);

        assertFalse(study.isJoinable(userAccount));
    }

    @DisplayName("스터디 공개중, 모집중, 스터디 멤버면 가입 불가능")
    @Test
    void isJoinable_false_for_member() {
        study.setPublished(true);
        study.setRecruiting(true);
        study.addMember(account);

        assertFalse(study.isJoinable(userAccount));
    }

    @DisplayName("스터디 비공개, 모집중 아니면 스터디 가입 불가능")
    @Test
    void isJoinable_false_for_non_publishing_and_non_recruiting() {
        study.setPublished(false);
        study.setRecruiting(true);
        assertFalse(study.isJoinable(userAccount));

        study.setPublished(true);
        study.setRecruiting(false);
        assertFalse(study.isJoinable(userAccount));
    }

    @DisplayName("스터디 멤버 확인")
    @Test
    void isMember() {
        study.addMember(account);

        assertTrue(study.isMember(userAccount));
    }

    @DisplayName("스터디 관리자 확인")
    @Test
    void isManager() {
        study.addManager(account);

        assertTrue(study.isManager(userAccount));
    }
}
