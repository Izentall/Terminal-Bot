package com.gr3530904_90104.utils;

import com.gr3530904_90104.command.BotCommandType;
import com.gr3530904_90104.model.Settings;

public class CommandUtils {
    private CommandUtils() { }

    public static BotCommandType resolveCommand(String message) {
        String[] split = message.split("\\s");
        return BotCommandType.ofValue(split[0].substring(1));
    }

    public static Settings parseConnect(String message) {
        String[] split = message.split("\\s");
        if (split.length == 1) {
            return null;
        }
        String userLogin = split[1].substring(0, split[1].indexOf("@"));
        String host = split[1].substring(split[1].indexOf("@") + 1, split[1].indexOf(":"));
        String port = "22";

        if (userLogin.length() + host.length() + 1 < split[1].length()) {
            port = split[1].substring(split[1].indexOf(":") + 1);
        }

        String password = null;
        if (split.length == 3) {
            password = split[2];
        }

        return Settings.builder().userLogin(userLogin).host(host).port(Integer.parseInt(port)).password(password).build();
    }
}
