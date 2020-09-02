package com.example.chatting;
 
public class Users {
    private String UserId;
    private String UserName;
    private String UserStatus;
    private String UserImage;

    public Users(){

    }

    public Users(String userName, String userStatus, String UserImage, String UserID) {
        UserName = userName;
        UserStatus = userStatus;
        this.UserImage = UserImage;
        this.UserId=UserID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserStatus() {
        return UserStatus;
    }

    public void setUserStatus(String userStatus) {
        UserStatus = userStatus;
    }

    public String getUserImage() {
        return this.UserImage;
    }

    public void setUserImage(String UserImage) {
        this.UserImage = UserImage;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}
