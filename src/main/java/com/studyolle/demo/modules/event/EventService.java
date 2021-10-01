package com.studyolle.demo.modules.event;

import com.studyolle.demo.modules.event.form.EventForm;
import com.studyolle.demo.modules.account.Account;
import com.studyolle.demo.modules.study.Study;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

    public Event createNewEvent(Event event, Study study, Account account) {
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setStudy(study);
        return eventRepository.save(event);
    }

    public void updateEvent(Event event, EventForm eventForm) {
        modelMapper.map(eventForm, event);
        event.acceptWaitingList();
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
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
    }

    public void rejectEnrollment(Event event, Enrollment enrollment) {
        event.reject(enrollment);
    }
}
