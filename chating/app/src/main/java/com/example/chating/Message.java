package com.example.chating;

public class Message {

    private String senderMessage;
    private String receiverMessage;
    private String messageType;
    private int SeenStateNum;

    public Message(String senderMessage,String receiverMessage, String messageType, int SeenStateNum){
        this.senderMessage=senderMessage;
        this.receiverMessage=receiverMessage;
        this.messageType=messageType;
        this.SeenStateNum=SeenStateNum;
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

    public int getSeenStateNum() {
        return SeenStateNum;
    }


}
