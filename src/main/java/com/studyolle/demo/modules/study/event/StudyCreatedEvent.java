package com.studyolle.demo.modules.study.event;

import com.studyolle.demo.modules.study.Study;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StudyCreatedEvent {

    private final Study study;

}
