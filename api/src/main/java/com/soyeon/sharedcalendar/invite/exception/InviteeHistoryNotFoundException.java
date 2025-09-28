package com.soyeon.sharedcalendar.invite.exception;

import com.soyeon.sharedcalendar.common.exception.BusinessException;
import com.soyeon.sharedcalendar.common.exception.ErrorCode;

public class InviteeHistoryNotFoundException extends BusinessException {
    public InviteeHistoryNotFoundException() {
        super(ErrorCode.InviteeHistoryNotFound, "존재하지 않는 초대 이력 입니다.");
    }
}
