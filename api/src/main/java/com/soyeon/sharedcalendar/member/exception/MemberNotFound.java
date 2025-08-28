package com.soyeon.sharedcalendar.member.exception;

import com.soyeon.sharedcalendar.common.exception.BusinessException;
import com.soyeon.sharedcalendar.common.exception.ErrorCode;

public class MemberNotFound extends BusinessException {
    public MemberNotFound(Long memberId) {
        super(ErrorCode.MEMBER_NOT_FOUND, "존재하지 않는 회원입니다.(memberId: " + memberId + ")");
    }
}
