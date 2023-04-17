package com.gr3530904_90104.bot;

import com.gr3530904_90104.command.BotCommandType;
import com.gr3530904_90104.model.User;
import com.gr3530904_90104.service.TerminalService;
import com.gr3530904_90104.utils.CommandUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Getter
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;
    private final TerminalService terminalService;

    public TelegramBot(
            TelegramBotsApi telegramBotsApi,
            @Value("${bot.username}") String botUsername,
            @Value("${bot.token}") String botToken,
            TerminalService terminalService
    ) throws TelegramApiException {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.terminalService = terminalService;

        telegramBotsApi.registerBot(this);
    }

    /**
     * Этот метод вызывается при получении обновлений через метод GetUpdates.
     *
     * @param request Получено обновление
     */
    @SneakyThrows
    @Override
    public void onUpdateReceived(Update request) {
        User user = terminalService.getUser(request.getMessage().getChat());
        try {
            String answer = terminalService.executeBotCommand(user, request.getMessage().getText());
            sendAnswer(user, answer);
        } catch (IllegalArgumentException e) {
            sendAnswer(user, e.getMessage());
        }
    }

    /**
     * Отправка ответа
     * @param user пользователь
     * @param text текст ответа
     */
    private void sendAnswer(User user, String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(user.getChatId().toString());
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            //логируем сбой Telegram Bot API, используя userName
        }
    }
}
