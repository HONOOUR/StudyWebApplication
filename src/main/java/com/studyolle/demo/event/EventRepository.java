package com.studyolle.demo.event;

import com.studyolle.demo.domain.Event;
import com.studyolle.demo.domain.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {

    @EntityGraph(value = "Event.withEnrollments")
    List<Event> findByStudyOrderByStartDateTime(Study study);
}
