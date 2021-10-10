package com.studyolle.demo.modules.event.event;

import com.studyolle.demo.infra.config.AppProperties;
import com.studyolle.demo.infra.mail.EmailMessage;
import com.studyolle.demo.infra.mail.EmailService;
import com.studyolle.demo.modules.account.Account;
import com.studyolle.demo.modules.account.AccountPredicates;
import com.studyolle.demo.modules.account.AccountRepository;
import com.studyolle.demo.modules.event.Enrollment;
import com.studyolle.demo.modules.event.EnrollmentRepository;
import com.studyolle.demo.modules.event.Event;
import com.studyolle.demo.modules.event.EventRepository;
import com.studyolle.demo.modules.notification.Notification;
import com.studyolle.demo.modules.notification.NotificationRepository;
import com.studyolle.demo.modules.notification.NotificationType;
import com.studyolle.demo.modules.study.Study;
import com.studyolle.demo.modules.study.StudyRepository;
import com.studyolle.demo.modules.study.event.StudyUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class EnrollmentEventListener {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;
    private final EnrollmentRepository enrollmentRepository;

    @EventListener
    public void handleEnrollmentEvent(EnrollmentEvent enrollmentEvent) {
        // enrollment change to account

        // event handler 에 enrollment 는 detached 객체임
        // 필요한 정보를 모두 가지고 있는 객체를 핸들러에 넘기거나
        // 핸들러에서 조회해야함

        Enrollment enrollment = enrollmentEvent.getEnrollment();
        Account account = enrollment.getAccount();
        Event event = enrollment.getEvent();
        Study study = event.getStudy();

        if (account.isStudyEnrollmentResultByEmail()) {
            sendEmail(study, account, event, enrollmentEvent);
        }

        if (account.isStudyEnrollmentResultByWeb()) {
            createNotification(study, account, event, enrollmentEvent);
        }
    }

    private void createNotification(Study study, Account account, Event event, EnrollmentEvent enrollmentEvent) {
        Notification notification = new Notification();
        notification.setTitle(event.getTitle());
        notification.setLink("/study/" + study.getEncodedPath() + "/events/" + event.getId());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(enrollmentEvent.getMessage());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.EVENT_ENROLLMENT);
        notificationRepository.save(notification);
    }

    private void sendEmail(Study study, Account account, Event event, EnrollmentEvent enrollmentEvent) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study/" + study.getEncodedPath() + "/events/" + event.getId());
        context.setVariable("linkName", event.getTitle());
        context.setVariable("message", enrollmentEvent.getMessage());
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("mail/simple-link", context);
        EmailMessage emailMessage = EmailMessage.builder()
                .subject(event.getTitle() + " 모임 참가 신청 결과 입니다.")
                .to(account.getEmail())
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);
    }
}
