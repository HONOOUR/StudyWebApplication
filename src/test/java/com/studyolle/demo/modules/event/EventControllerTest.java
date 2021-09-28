package com.studyolle.demo.modules.event;

import com.studyolle.demo.domain.*;
import com.studyolle.demo.event.EnrollmentRepository;
import com.studyolle.demo.event.EventService;
import com.studyolle.demo.infra.AbstractContainerBaseTest;
import com.studyolle.demo.infra.MockMvcTest;
import com.studyolle.demo.modules.account.AccountFactory;
import com.studyolle.demo.modules.account.AccountRepository;
import com.studyolle.demo.modules.account.WithAccount;
import com.studyolle.demo.modules.study.StudyFactory;
import com.studyolle.demo.modules.study.StudyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
public class EventControllerTest extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private StudyService studyService;
    @Autowired
    private EventService eventService;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private AccountFactory accountFactory;
    @Autowired
    private StudyFactory studyFactory;

    @Test
    @DisplayName("제한 인원 보다 참가자 적은 모임 참가 신청 수락")
    @WithAccount("jieun")
    void joinEvent_less_limit_success() throws Exception {
        Account hyok = accountFactory.createAccount("hyok");
        Study study = studyFactory.createStudy("test-path", hyok);
        Event event = createEvent(study, hyok, EventType.FCFS, 2);

        mockMvc.perform(post("/study/" + study.getPath()+ "/events/" + event.getId() + "/enroll")
        // add an account to the join list
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath()+ "/events/" + event.getId()));

        Account jieun = accountRepository.findByNickname("jieun");
        assertTrue(enrollmentRepository.findByEventAndAccount(event, jieun).isAccepted());
    }

    @Test
    @DisplayName("제한 인원보다 참가자 많은 모임 신청 수락 대기")
    @WithAccount("jieun")
    void joinEvent_more_limit_success() throws Exception {
        Account hyok = accountFactory.createAccount("hyok");
        Study study = studyFactory.createStudy("test-path", hyok);
        Event event = createEvent(study, hyok, EventType.FCFS, 2);
        Account person1 = accountFactory.createAccount("person1");
        Account person2 = accountFactory.createAccount("person2");

        eventService.enrollEvent(event, person1);
        eventService.enrollEvent(event, person2);

        mockMvc.perform(post("/study/" + study.getPath()+ "/events/" + event.getId() + "/enroll")
                // add an account to the join list
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath()+ "/events/" + event.getId()));

        Account jieun = accountRepository.findByNickname("jieun");
        assertFalse(enrollmentRepository.findByEventAndAccount(event, jieun).isAccepted());
    }

    @Test
    @DisplayName("제한 인원보다 참가자 많은 모임 신청 수락 취소 다음 대기자 수락 됨")
    @WithAccount("jieun")
    void joinEvent_wait_and_accepted_success() throws Exception {
        Account hyok = accountFactory.createAccount("hyok");
        Study study = studyFactory.createStudy("test-path", hyok);
        Event event = createEvent(study, hyok, EventType.FCFS, 2);
        Account person1 = accountFactory.createAccount("person1");
        Account person2 = accountFactory.createAccount("person2");

        eventService.enrollEvent(event, person1);
        Account jieun = accountRepository.findByNickname("jieun");
        eventService.enrollEvent(event, jieun);
        eventService.enrollEvent(event, person2);

        assertFalse(enrollmentRepository.findByEventAndAccount(event, person2).isAccepted());

        mockMvc.perform(post("/study/" + study.getPath()+ "/events/" + event.getId() + "/disenroll")
                // add an account to the join list
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath()+ "/events/" + event.getId()));

        assertTrue(enrollmentRepository.findByEventAndAccount(event, person2).isAccepted());
    }

    @Test
    @DisplayName("관리자 확인 모임 신청 수락 성공")
    @WithAccount("jieun")
    void joinEvent_confirmative_wait_and_accepted_success() throws Exception {
        Account hyok = accountFactory.createAccount("hyok");
        Study study = studyFactory.createStudy("test-path", hyok);
        Event event = createEvent(study, hyok, EventType.CONFIRMATIVE, 2);
        Account person1 = accountFactory.createAccount("person1");
        eventService.enrollEvent(event, person1);
        assertFalse(enrollmentRepository.findByEventAndAccount(event, person1).isAccepted());

        mockMvc.perform(post("/study/" + study.getPath()+ "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath()+ "/events/" + event.getId()));

        Account jieun = accountRepository.findByNickname("jieun");
        assertFalse(enrollmentRepository.findByEventAndAccount(event, jieun).isAccepted());
    }

    private Event createEvent(Study study, Account hyok, EventType eventType, int limitOfEnrollment) {
        Event event = new Event();
        event.setStudy(study);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setCreatedBy(hyok);
        event.setEventType(eventType);
        event.setStartDateTime(LocalDateTime.now().plusDays(6));
        event.setEndDateTime(LocalDateTime.now().plusDays(10));
        event.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(5));
        event.setLimitOfEnrollments(limitOfEnrollment);
        event.setTitle("testEvent");
        eventService.createNewEvent(event, study, hyok);
        return event;
    }

}
