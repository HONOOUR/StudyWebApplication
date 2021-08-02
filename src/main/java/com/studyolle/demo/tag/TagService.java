package com.studyolle.demo.tag;

import com.studyolle.demo.domain.Study;
import com.studyolle.demo.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {
    private TagRepository tagRepository;

    public Tag findOrCreateNew(String tagTitle) {
        Tag tag = tagRepository.findByTitle(tagTitle);
        if (tag == null) {
            tagRepository.save(tag);
        }
        return tag;
    }

}
