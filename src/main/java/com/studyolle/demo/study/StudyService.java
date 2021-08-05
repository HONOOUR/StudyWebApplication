package com.studyolle.demo.study;

import com.studyolle.demo.domain.Account;
import com.studyolle.demo.domain.Study;
import com.studyolle.demo.domain.Tag;
import com.studyolle.demo.domain.Zone;
import com.studyolle.demo.study.form.StudyDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;

    public Study createNewStudy(Study study, Account account) {
        Study newStudy = studyRepository.save(study);
        newStudy.addManager(account);

        return newStudy;
    }

    public Study getStudyToUpdate(Account account, String path) {
        Study study = getStudy(path);
        checkIfManager(account, study);

        return study;
    }

    private void checkIfManager(Account account, Study study) {
        if (!account.isManagerOf(study, account)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    public Study getStudy(String path) {
        Study study = this.studyRepository.findByPath(path);
        checkIfExistingStudy(path, study);

        return study;
    }

    private void checkIfExistingStudy(String path, Study study) {
        if (study == null) {
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 없습니다.");
        }
    }

    public void updateStudyDescription(Study study, StudyDescriptionForm studyDescriptionForm) {
        modelMapper.map(studyDescriptionForm, study);
    }

    public void updateStudyBannerImage(Study study, String image) {
        study.setImage(image);
    }

    public void updateStudyBannerImageStatus(Study study) {
        Boolean isEnabled = study.isUseBanner();
        study.setUseBanner(!isEnabled);
    }

    public Set<Tag> getTags(Study study) {
        Set<Tag> tags = study.getTags();
        return tags;
    }

    public Set<Zone> getZones(Study study) {
        Set<Zone> zones = study.getZones();
        return zones;
    }

    public void addTag(Study study, Tag tag) {
        study.getTags().add(tag);
    }

    public void removeTag(Study study, Tag tag) {
        study.getTags().remove(tag);
    }

    public void addZone(Study study, Zone zone) {
        study.getZones().add(zone);
    }

    public void removeZone(Study study, Zone zone) {
        study.getZones().remove(zone);
    }

    public void close(Study study) {
        study.close();
    }

    public void publish(Study study) {
        study.publish();
    }

    public void startRecruit(Study study) {
        study.startRecruit(study);
    }

    public void stopRecruit(Study study) {
        study.stopRecruit(study);
    }

}
