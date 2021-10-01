package com.studyolle.demo.modules.study;

import com.studyolle.demo.infra.AbstractContainerBaseTest;
import com.studyolle.demo.infra.MockMvcTest;
import com.studyolle.demo.modules.account.WithAccount;
import com.studyolle.demo.modules.account.AccountRepository;
import com.studyolle.demo.modules.account.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
public class StudySettingsControllerTest extends AbstractContainerBaseTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private AccountRepository accountRepository;
    @Autowired private StudyFactory studyFactory;

    @Test
    @WithAccount("jieun")
    @DisplayName("스터디 소개 조회 - 실패 (권한 없는 유저)")
    void viewStudySettings_fail() throws Exception {
        // 권한이 있는 유저로 스터디 생성
        Account account = accountRepository.findByNickname("unauthorized");
        Study study = studyFactory.createStudy("test-path", account);
        mockMvc.perform(get("/study/" + study.getPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }

    @Test
    @WithAccount("jieun")
    @DisplayName("스터디 소개 조회 - 성공")
    void viewStudySettings_success() throws Exception {
        // 권한이 있는 유저로 스터디 생성
        Account account = accountRepository.findByNickname("jieun");
        Study study = studyFactory.createStudy("test-path", account);
        mockMvc.perform(get("/study/" + study.getPath() + "/settings/description"))
                // check status (redirection
                // dispaly the view
                // with view name
                // with model attributes
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("/study/test-path/settings/description"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @WithAccount("jieun")
    @DisplayName("스터디 소개 업데이트 - 실패, no blank")
    void updateStudyInfo_fail() throws Exception {
        // 권한이 있는 유저로 스터디 생성
        Account account = accountRepository.findByNickname("jieun");
        Study study = studyFactory.createStudy("test-path", account);

        mockMvc.perform(post("/study/" + study.getPath() + "/settings/description")
                .param("shortDescription", "")
                .param("fullDescription", "full description")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("studyDescriptionForm"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("account"));
    }

    @Test
    @WithAccount("jieun")
    @DisplayName("스터디 소개 업데이트 - 성공, no blank")
    void updateStudyInfo_success() throws Exception {
        // 권한이 있는 유저로 스터디 생성
        Account account = accountRepository.findByNickname("jieun");
        Study study = studyFactory.createStudy("test-path", account);

        String studyDescriptionUrl = "/study/" + study.getPath() + "/settings/description";
        mockMvc.perform(post(studyDescriptionUrl)
                .param("shortDescription", "short description")
                .param("fullDescription", "full description")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(studyDescriptionUrl))
                .andExpect(flash().attributeExists("message"));
    }
}
