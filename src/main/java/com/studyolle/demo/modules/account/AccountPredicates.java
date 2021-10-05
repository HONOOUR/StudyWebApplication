package com.studyolle.demo.modules.account;

import com.querydsl.core.types.Predicate;
import com.studyolle.demo.modules.zone.Zone;
import com.studyolle.demo.modules.tag.Tag;

import java.util.Set;

public class AccountPredicates {

    public static Predicate findByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
        QAccount account = QAccount.account;
        return QAccount.account.zones.any().in(zones).and(account.tags.any().in(tags));
    }

}
