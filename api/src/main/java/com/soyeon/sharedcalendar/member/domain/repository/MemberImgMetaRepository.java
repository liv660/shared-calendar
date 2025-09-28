package com.soyeon.sharedcalendar.member.domain.repository;

import com.soyeon.sharedcalendar.member.domain.img.MemberImgMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberImgMetaRepository extends JpaRepository<MemberImgMeta,Long> {

}
