package com.studyolle.demo.main;


import com.studyolle.demo.account.AccountService;
import com.studyolle.demo.account.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
// 사용할 수 없음 Junit 먼저관여 @RequiredArgsConstructor
public class MainControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired AccountService accountService;

    @Test
    void login_with_email() throws Exception {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("jieun");
        signUpForm.setEmail("jieun@icloud.com");
        signUpForm.setPassword("12341234");
        accountService.processNewAccount(signUpForm);
        
        mockMvc.perform(post("/login")
                .param("username", "jieun@icloud.com")
                .param("password", "12341234")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}

