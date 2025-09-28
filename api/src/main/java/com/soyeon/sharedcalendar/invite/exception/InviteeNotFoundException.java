package com.soyeon.sharedcalendar.invite.exception;

import com.soyeon.sharedcalendar.common.exception.BusinessException;
import com.soyeon.sharedcalendar.common.exception.ErrorCode;

public class InviteeNotFoundException extends BusinessException {
    public InviteeNotFoundException() {
        super(ErrorCode.InviteeNotFound, "존재하지 않는 초대 토큰 입니다.");
    }
}
