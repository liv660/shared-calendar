package com.soyeon.sharedcalendar.invite.api;

import com.soyeon.sharedcalendar.invite.app.InviteService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/invite")
public class InviteController {
    private final InviteService inviteService;

    @Value("${app.front.base-url}")
    private String redirectUrl;

    @GetMapping("/accept/{inviteToken}")
    public void accept(@PathVariable String inviteToken, HttpServletResponse res) throws IOException {
        inviteService.accept(inviteToken);
        res.sendRedirect(redirectUrl);
    }
}
