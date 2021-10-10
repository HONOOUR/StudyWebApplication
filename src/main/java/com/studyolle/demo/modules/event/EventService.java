package com.studyolle.demo.modules.event;

import com.studyolle.demo.modules.event.event.EnrollmentAcceptedEvent;
import com.studyolle.demo.modules.event.event.EnrollmentRejectedEvent;
import com.studyolle.demo.modules.event.form.EventForm;
import com.studyolle.demo.modules.account.Account;
import com.studyolle.demo.modules.study.Study;
import com.studyolle.demo.modules.study.event.StudyUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Event createNewEvent(Event event, Study study, Account account) {
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setStudy(study);
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(), "'" + event.getTitle() + "' 모임을 만들었습니다."));
        return eventRepository.save(event);
    }

    public void updateEvent(Event event, EventForm eventForm) {
        modelMapper.map(eventForm, event);
        event.acceptWaitingList();
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(), "'" + event.getTitle() + "' 모임을 정보를 수정했으니 확인하세요."));

    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(), "'" + event.getTitle() + "' 모임을 취소했습니다."));
    }

    public void enrollEvent(Event event, Account account) {
        //  이 이벤트에 추가하려고 하는 enrollment 가 이미 있는지 확인
        if (!enrollmentRepository.existsByEventAndAccount(event, account)) {
            Enrollment enrollment = new Enrollment();
            enrollment.setAccount(account);
            enrollment.setEvent(event);
            enrollment.setEnrolledAt(LocalDateTime.now());
            enrollment.setAccepted(event.isAbleToAcceptWaitingEnrollment());
            event.addEnrollment(enrollment);
            enrollmentRepository.save(enrollment);
        }
    }

    public void disenrollEvent(Event event, Account account) {
        if (enrollmentRepository.existsByEventAndAccount(event, account)) {
            Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
            event.removeEnrollment(enrollment);
            enrollmentRepository.delete(enrollment);
            event.acceptNextWaitingEnrollment();
        }
    }

    public void acceptEnrollment(Event event, Enrollment enrollment) {
        event.accept(enrollment);
        eventPublisher.publishEvent(new EnrollmentAcceptedEvent(enrollment));
    }

    public void rejectEnrollment(Event event, Enrollment enrollment) {
        event.reject(enrollment);
        eventPublisher.publishEvent(new EnrollmentRejectedEvent(enrollment));
    }
}
