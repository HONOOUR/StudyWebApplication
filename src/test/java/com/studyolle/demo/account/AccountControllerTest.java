package com.studyolle.demo.account;

import com.studyolle.demo.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.any;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private JavaMailSender javaMailSender;

    @DisplayName("Sign in - page shown")
    @Test
    void signUpForm() throws Exception{
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"));
    }

    @DisplayName("Sign in - input error")
    @Test
    void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "jieun")
                .param("email", "email...")
                .param("password", "123")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));
    }

    @DisplayName("Sign in - input correct")
    @Test
    void signUpSubmit_with_correct_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "jieun")
                .param("email", "hearful@icloud.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
        Account account = accountRepository.findByEmail("hearful@icloud.com");
        assertNotNull(account);
        assertNotEquals(account.getPassword(), "12345678");

        // access database
        assertTrue(accountRepository.existsByEmail("hearful@icloud.com"));
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }
}
