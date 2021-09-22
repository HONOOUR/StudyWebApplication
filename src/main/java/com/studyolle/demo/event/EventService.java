package com.studyolle.demo.event;

import com.studyolle.demo.domain.Account;
import com.studyolle.demo.domain.Event;
import com.studyolle.demo.domain.Study;
import com.studyolle.demo.event.form.EventForm;
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


    public Event createNewEvent(Event event, Study study, Account account) {
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setStudy(study);
        return eventRepository.save(event);
    }

    public void updateEvent(Event event, EventForm eventForm) {
        modelMapper.map(eventForm, event);
        eventRepository.save(event);
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }
}
