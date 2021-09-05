package com.studyolle.demo.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Event {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Study study;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = true)
    private int limitOfEnrollments;

    @OneToMany(mappedBy = "event")
    private List<Enrollement> enrollements;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

}
