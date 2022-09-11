package com.example.applichatlimayrac.chat;

public class ChatList {

    private String sUsername;
    private String sDateTime;
    private String sMessage;
    private String sUserColor;
    private Boolean hasImage;
    private String sImage;

    public ChatList(String sUsername, String sDateTime, String sMessage, String sUserColor, Boolean hasImage, String sImage) {
        this.sUsername = sUsername;
        this.sDateTime = sDateTime;
        this.sMessage = sMessage;
        this.sUserColor = sUserColor;
        this.hasImage = hasImage;
        this.sImage = sImage;
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

    public Boolean getHasImage() {
        return hasImage;
    }

    public void setHasImage(Boolean hasImage) {
        this.hasImage = hasImage;
    }

    public String getsImage() {
        return sImage;
    }

    public void setsImage(String sImage) {
        this.sImage = sImage;
    }
}
