package com.studyolle.demo.modules.main;

import com.studyolle.demo.modules.account.CurrentAccount;
import com.studyolle.demo.modules.account.Account;
import com.studyolle.demo.modules.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final NotificationRepository notificationRepository;

    // dynamically get principal
    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
