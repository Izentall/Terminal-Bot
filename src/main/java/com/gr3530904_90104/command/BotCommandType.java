package com.gr3530904_90104.command;

public enum BotCommandType {
    START("start"),
    CONNECT("connect"),
    EXECUTE("execute"),
    CLOSE("close");

    private final String value;

    private BotCommandType(String value) {
        this.value = value;
    }

    public static BotCommandType ofValue(String value) {
        for (BotCommandType botCommandType : BotCommandType.values()) {
            if (botCommandType.getValue().equals(value)) {
                return botCommandType;
            }
        }
        throw new IllegalArgumentException("No such command as " + value);
    }

    public String getValue() {
        return value;
    }
}
