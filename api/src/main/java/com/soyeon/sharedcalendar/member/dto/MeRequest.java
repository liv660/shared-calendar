package com.soyeon.sharedcalendar.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.soyeon.sharedcalendar.common.img.dto.ImgRequest;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MeRequest(String name,
                        ImgRequest imgMeta) {
}
