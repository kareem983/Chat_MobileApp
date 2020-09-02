package com.example.chatting;
 
public class Friends {
    private String FriendId;
    private String FriendName;
    private String FriendDate;
    private String FriendImage;
    private final int FriendOnlineIcon=R.drawable.online;
    public boolean IsOnline;

    public Friends(){
    }

    public Friends(String friendName, String friendDate, String friendImage, String friendId, boolean IsOnline ) {
        FriendName = friendName;
        FriendDate = friendDate;
        FriendImage = friendImage;
        FriendId = friendId;
        this.IsOnline=IsOnline;
    }

    public String getFriendId() {
        return FriendId;
    }

    public void setFriendId(String friendId) {
        FriendId = friendId;
    }

    public String getFriendName() {
        return FriendName;
    }

    public void setFriendName(String friendName) {
        FriendName = friendName;
    }

    public String getFriendDate() {
        return FriendDate;
    }

    public void setFriendDate(String friendDate) {
        FriendDate = friendDate;
    }

    public String getFriendImage() {
        return FriendImage;
    }

    public void setFriendImage(String friendImage) {
        FriendImage = friendImage;
    }

    public int getFriendOnlineIcon() {
        return FriendOnlineIcon;
    }

    public boolean isOnline() {
        return IsOnline;
    }

    public void setOnline(boolean online) {
        IsOnline = online;
    }
}
