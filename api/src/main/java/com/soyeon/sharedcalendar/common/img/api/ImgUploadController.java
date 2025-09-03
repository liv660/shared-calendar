package com.soyeon.sharedcalendar.common.img.api;

import com.soyeon.sharedcalendar.common.img.app.ImgUploadService;
import com.soyeon.sharedcalendar.common.img.dto.ImgUploadResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/img")
@RequiredArgsConstructor
@Tag(name = "Image", description = "이미지 업로드")
public class ImgUploadController {
    private final ImgUploadService imgUploadService;

    /**
     * object key, presigned url 생성
     * @param contentType mime
     * @return
     */
    @PostMapping("/presigned")
    public ImgUploadResponse getPresignedUrl(@RequestParam String contentType) {
        return imgUploadService.getPresignedUrl(contentType);
    }
}
