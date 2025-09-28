package com.soyeon.sharedcalendar.temp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Tag(name = "임시 API", description = "테스트 access_token 발급 API")
public class TemporalTokenController {
    private final TempTokenService tempTokenService;

    @GetMapping("/temp")
    @Operation(summary = "테스트용 access_token 발급")
    public String accessTokenForTest() {
        return tempTokenService.issueToken();
    }
}
