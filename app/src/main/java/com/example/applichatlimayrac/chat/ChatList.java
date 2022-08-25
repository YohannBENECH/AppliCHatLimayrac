package com.example.applichatlimayrac.chat;

public class ChatList {

    private String sUsername;
    private String sDateTime;
    private String sMessage;
    private String sUserColor;

    public ChatList(String sUsername, String sDateTime, String sMessage, String sUserColor) {
        this.sUsername = sUsername;
        this.sDateTime = sDateTime;
        this.sMessage = sMessage;
        this.sUserColor = sUserColor;
    }

    public String getsUsername() {
        return sUsername;
    }

    public void setsUsername(String sUsername) {
        this.sUsername = sUsername;
    }

    public String getsDateTime() {
        return sDateTime;
    }

    public void setsDateTime(String sDateTime) {
        this.sDateTime = sDateTime;
    }

    public String getsMessage() {
        return sMessage;
    }

    public void setsMessage(String sMessage) {
        this.sMessage = sMessage;
    }

    public String getsUserColor() {
        return sUserColor;
    }

    public void setsUserColor(String sUserColor) {
        this.sUserColor = sUserColor;
    }
}
