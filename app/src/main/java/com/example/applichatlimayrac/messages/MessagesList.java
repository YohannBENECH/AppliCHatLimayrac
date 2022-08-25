package com.example.applichatlimayrac.messages;

public class MessagesList {

    private String sDateTime;
    private String sUsername;
    private String sLastMessage;
    private String sProfilePic;
    private String sUserColor;

    private int iUnseenMessages;


    // -----------------------------------------------------------------------------------------------------
    public MessagesList(String sUsername, String sLastMessage, String sProfilePic, int iUnseenMessages, String sDateTime, String sUserColor) {
        this.sUsername = sUsername;
        this.sLastMessage = sLastMessage;
        this.sProfilePic = sProfilePic;
        this.iUnseenMessages = iUnseenMessages;
        this.sDateTime = sDateTime;
        this.sUserColor = sUserColor;
    }

    public String getsUsername() {
        return sUsername;
    }

    public void setsUsername(String sUsername) {
        this.sUsername = sUsername;
    }

    public String getsLastMessage() {
        return sLastMessage;
    }

    public void setsLastMessage(String sLastMessage) {
        this.sLastMessage = sLastMessage;
    }

    public String getsProfilePic() {
        return sProfilePic;
    }

    public void setsProfilePic(String sProfilePic) {
        this.sProfilePic = sProfilePic;
    }

    public int getiUnseenMessages() {
        return iUnseenMessages;
    }

    public void setiUnseenMessages(int iUnseenMessages) {
        this.iUnseenMessages = iUnseenMessages;
    }

    public String getsDateTime() {
        return sDateTime;
    }

    public void setsDateTime(String sDateTime) {
        this.sDateTime = sDateTime;
    }

    public String getsUserColor() {
        return sUserColor;
    }

    public void setsUserColor(String sUserColor) {
        this.sUserColor = sUserColor;
    }
}
