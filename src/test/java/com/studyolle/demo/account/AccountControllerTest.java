package com.studyolle.demo.account;

import com.studyolle.demo.domain.Account;
import com.studyolle.demo.mail.EmailMessage;
import com.studyolle.demo.mail.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.any;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private EmailService emailService;

    @DisplayName("Authentication mail - input error")
    @Test
    void checkEmailToken_with_wrong_input() throws Exception{
        mockMvc.perform(get("/check-email-token")
                .param("token", "sdfasdfasf")
                .param("email", "email@email.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }

    @DisplayName("Authentication mail - input correct")
    @Test
    void checkEmailToken() throws Exception{
        Account account = Account.builder()
                .email("test@email.com")
                .password("12345678")
                .nickname("jieun")
                .build();
        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                .param("token", newAccount.getEmailCheckToken())
                .param("email", newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());
    }

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
        then(emailService).should().sendEmail(any(EmailMessage.class));
    }
}
