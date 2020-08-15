package com.example.chating;

public class Message {

    private String senderMessage;
    private String receiverMessage;
    private String messageType;
    private boolean IsImage;
    private Integer SeenStateNum;

    public Message(String senderMessage,String receiverMessage, String messageType, Integer SeenStateNum, boolean IsImage){
        this.senderMessage=senderMessage;
        this.receiverMessage=receiverMessage;
        this.messageType=messageType;
        this.SeenStateNum=SeenStateNum;
        this.IsImage=IsImage;
    }


    public String getSenderMessage() {
        return senderMessage;
    }

    public String getReceiverMessage() {
        return receiverMessage;
    }

    public String getMessageType() {
        return messageType;
    }

    public Integer getSeenStateNum() {
        return SeenStateNum;
    }

    public boolean isImage() {
        return IsImage;
    }
}
