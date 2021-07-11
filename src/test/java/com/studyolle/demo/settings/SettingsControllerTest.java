package com.studyolle.demo.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.demo.WithAccount;
import com.studyolle.demo.account.AccountRepository;
import com.studyolle.demo.account.AccountService;
import com.studyolle.demo.domain.Account;
import com.studyolle.demo.domain.Tag;
import com.studyolle.demo.domain.Zone;
import com.studyolle.demo.settings.form.TagForm;
import com.studyolle.demo.settings.form.ZoneForm;
import com.studyolle.demo.tag.TagRepository;
import com.studyolle.demo.zone.ZoneRepository;
import org.aspectj.lang.annotation.After;
import org.h2.engine.Setting;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    ZoneRepository zoneRepository;

    private Zone testZone = Zone.builder().city("testCity").localNameOfCity("테스트시").province("테스트주").build();

    @Autowired
    void beforeEach() {
        zoneRepository.save(testZone);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
        zoneRepository.deleteAll();
    }

    @WithAccount("jieun")
    @DisplayName("프로필 수정 폼 정상")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("jieun")
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "찗은 소개를 수정하는 경우.";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account jieun = accountRepository.findByNickname("jieun");
        assertEquals(bio, jieun.getBio());
    }

    @WithAccount("jieun")
    @DisplayName("프로필 수정하기 - 입력값 오류")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "길게 소개를 수정하는 경우.길게 소개를 수정하는 경우.길게 소개를 수정하는 경우.길게 소개를 수정하는 경우.길게 소개를 수정하는 경우.길게 소개를 수정하는 경우.길게 소개를 수정하는 경우.길게 소개를 수정하는 경우.길게 소개를 수정하는 경우.";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account jieun = accountRepository.findByNickname("jieun");
        assertNull(jieun.getBio());
    }

    @WithAccount("jieun")
    @DisplayName("패스워드 수정 폼")
    @Test
    void updatePassword_form() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("jieun")
    @DisplayName("비밀번호 수정하기 - 입력값 정상")
    @Test
    void updatePassword_success() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                .param("newPassword", "12341234")
                .param("newPasswordConfirm", "12341234")
                .with(csrf()))
                .andExpect(status().is3xxRedirection()) // redirect
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL)) // view name
                .andExpect(flash().attributeExists("message")); // message
        Account jieun = accountRepository.findByNickname("jieun");
        assertTrue(passwordEncoder.matches("12341234", jieun.getPassword()));
    }

    @WithAccount("jieun")
    @DisplayName("비밀번호 수정하기 - 입력값 에러")
    @Test
    void updatePassword_fail() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                .param("newPassword", "12341234")
                .param("newPasswordConfirm", "11111111")
                .with(csrf()))
                .andExpect(status().isOk()) // redirect
                .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME)) // view name
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
        Account jieun = accountRepository.findByNickname("jieun");
        assertTrue(passwordEncoder.matches("12341234", jieun.getPassword()));
    }

    @WithAccount("jieun")
    @DisplayName("태그 수정 폼")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_TAGS_URL))
                .andExpect(view().name(SettingsController.SETTINGS_TAGS_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithAccount("jieun")
    @DisplayName("계정에 태그 추가")
    @Test
    void addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(SettingsController.SETTINGS_TAGS_URL + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
        assertTrue(accountRepository.findByNickname("jieun").getTags().contains(newTag));
    }

    @WithAccount("jieun")
    @DisplayName("계정에 태그 삭제")
    @Test
    void removeTag() throws Exception {
        Account jieun = accountRepository.findByNickname("jieun");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(jieun, newTag);

        assertTrue(jieun.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(SettingsController.SETTINGS_TAGS_URL + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());
        
        assertFalse(accountRepository.findByNickname("jieun").getTags().contains(newTag));
    }

    @WithAccount("jieun")
    @DisplayName("지역 수정 폼")
    @Test
    void updateZoneForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_ZONE_URL))
                .andExpect(view().name(SettingsController.SETTINGS_ZONE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("zones"));
    }

    @WithAccount("jieun")
    @DisplayName("지역 태그 추가")
    @Test
    void addZone() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(SettingsController.SETTINGS_ZONE_URL + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Account jieun = accountRepository.findByNickname("jieun");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        assertTrue(jieun.getZones().contains(zone));
    }


    @WithAccount("jieun")
    @DisplayName("지역 태그 삭제")
    @Test
    void removeZone() throws Exception {
        Account jieun = accountRepository.findByNickname("jieun");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        accountService.addZone(jieun, zone);

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(SettingsController.SETTINGS_ZONE_URL + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(jieun.getZones().contains(zone));
    }
}