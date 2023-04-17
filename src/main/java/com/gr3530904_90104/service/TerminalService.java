package com.gr3530904_90104.service;

import com.gr3530904_90104.model.User;
import org.telegram.telegrambots.meta.api.objects.Chat;

public interface TerminalService {
    User getUser(Chat chat);

    String executeBotCommand(User user, String text);
}
