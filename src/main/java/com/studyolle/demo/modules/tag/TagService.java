package com.studyolle.demo.modules.tag;

import com.studyolle.demo.modules.zone.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
