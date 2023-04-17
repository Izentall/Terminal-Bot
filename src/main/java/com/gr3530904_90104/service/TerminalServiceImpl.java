package com.gr3530904_90104.service;

import com.gr3530904_90104.command.BotCommandType;
import com.gr3530904_90104.model.Settings;
import com.gr3530904_90104.model.User;
import com.gr3530904_90104.repository.SettingsRepository;
import com.gr3530904_90104.repository.UserRepository;
import com.gr3530904_90104.utils.CommandUtils;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class TerminalServiceImpl implements TerminalService {
    private final UserRepository userRepository;
    private final SettingsRepository settingsRepository;
    private final JSch jsch;
    private final String privateKeyPath;
    private final Map<User, Session> sessionMap;
    private final Map<User, Channel> channelMap;
    private final Map<User, InputStream> inputStreamMap;
    private final Map<User, OutputStream> outputStreamMap;

    public TerminalServiceImpl(
            UserRepository userRepository,
            SettingsRepository settingsRepository,
            @Value("${ssh.key.path}") String privateKeyPath
    ) {
        this.userRepository = userRepository;
        this.settingsRepository = settingsRepository;
        this.privateKeyPath = privateKeyPath;
        this.jsch = new JSch();
        this.sessionMap = new HashMap<>();
        this.channelMap = new HashMap<>();
        this.inputStreamMap = new HashMap<>();
        this.outputStreamMap = new HashMap<>();
    }

    public String executeBotCommand(User user, String message) {
        BotCommandType commandType = CommandUtils.resolveCommand(message);
        return switch (commandType) {
            case START -> "Старт";
            case CONNECT -> connect(user, message);
            case EXECUTE -> execute(user, message);
            case CLOSE -> close(user, message);
            default -> "Не готово";
        };
    }

    public String connect(User user, String message) {
        try {
            Settings settings = CommandUtils.parseConnect(message);
            if (settings == null) {
                settings = getSettings(user);
            } else {
                Settings s = getSettings(user);
                settings.setUserId(user.getId());
                settings.setId(s.getUserId() != 0 ? s.getId() : null);
                settings = settingsRepository.save(settings);
            }

            Session session = jsch.getSession(settings.getUserLogin(), settings.getHost(), settings.getPort());
            try {
                jsch.addIdentity(privateKeyPath);
                session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
            } catch (JSchException e) {
                throw new RuntimeException("Failed to create Jsch Session object.", e);
            }

            if (sessionMap.containsKey(user)) {
                close(user, message);
            }

            session.connect();
            sessionMap.put(user, session);

            ChannelShell channel = (ChannelShell) session.openChannel("shell");
            channel.setPty(false);
            channel.connect();
            channelMap.put(user, channel);
            channel.setOutputStream(System.out);

            InputStream in = channel.getInputStream();
            OutputStream out = channel.getOutputStream();

            inputStreamMap.put(user, in);
            outputStreamMap.put(user, out);

            Thread.sleep(1000);
            return getNextString(user);
        } catch (Exception e) {
            return "Получено исключение, невозможно подключиться";
        }
    }

    public String close(User user, String message) {
        Session session = sessionMap.remove(user);
        if (session != null) {
            outputStreamMap.remove(user);
            inputStreamMap.remove(user);
            channelMap.remove(user).disconnect();
            session.disconnect();
            return "Успешно отключились";
        }
        return "Нет подключения";
    }

    public String execute(User user, String message) {
        Session session = sessionMap.get(user);
        if (session == null) {
            return "Нет открытой сессии";
        }

        String[] split = message.split("\\s", 2);
        if (split.length != 2) {
            return "Передайте команду";
        }

        try {
            OutputStream out = outputStreamMap.get(user);

            // Отправляем команду на сервер
            out.write((split[1] + "\n").getBytes());
            out.flush();
            Thread.sleep(500);

            String result = getNextString(user);
            if (result.isEmpty()) {
                return "*Пустой вывод*";
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUser(Chat chat) {
        Optional<User> user = userRepository.findUserByChatId(chat.getId());
        return user.orElseGet(() -> userRepository.save(
                User.builder().username(chat.getUserName()).chatId(chat.getId()).build()));
    }

    public Settings getSettings(User user) {
        Optional<Settings> settings = settingsRepository.findSettingsByUserId(user.getId());
        return settings.orElseGet(() -> settingsRepository.findSettingsByUserId(0).get());
    }

    private String getNextString(User user) throws IOException {
        InputStream in = inputStreamMap.get(user);
        StringBuilder outputBuffer = new StringBuilder(1024);
        byte[] tmp = new byte[1024];
        while (in.available() > 0) {
            int i = in.read(tmp, 0, 1024);
            if (i < 0) break;
            String output = new String(tmp, 0, i);
            outputBuffer.append(output);
        }

        return outputBuffer.toString();
    }
}
