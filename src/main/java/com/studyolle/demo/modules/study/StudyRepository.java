package com.studyolle.demo.modules.study;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {

    boolean existsByPath(String path);

    @EntityGraph(attributePaths = {"tags", "zones", "managers", "members"},type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    @EntityGraph(attributePaths = { "tags", "managers" }, type = EntityGraph.EntityGraphType.LOAD)
    Study findStudyWithTagsByPath(String path);

    @EntityGraph(attributePaths = { "zones", "managers"}, type = EntityGraph.EntityGraphType.LOAD)
    Study findStudyWithZonesByPath(String path);

    @EntityGraph(attributePaths = "managers")
    Study findStudyWithManagersByPath(String path);

    @EntityGraph(attributePaths = "members")
    Study findStudyWithMembersByPath(String path);

    Study findStudyOnlyByPath(String path);

    @EntityGraph(value = "Study.withTagsAndZones", type = EntityGraph.EntityGraphType.LOAD)
    Study findStudyWithTagsAndZonesById(Long id);
}