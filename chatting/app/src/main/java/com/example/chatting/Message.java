package com.example.chatting;
 
public class Message {
    private String senderMessage;
    private String receiverMessage;
    private boolean IsSender;
    private Integer SeenStateNum;
    private String MessageType;
    private String MessageTime;

    public Message(String senderMessage,String receiverMessage, boolean IsSender, Integer SeenStateNum, String MessageType,String MessageTime){
        this.senderMessage=senderMessage;
        this.receiverMessage=receiverMessage;
        this.IsSender=IsSender;
        this.SeenStateNum=SeenStateNum;
        this.MessageType=MessageType;
        this.MessageTime=MessageTime;
    }


    public String getSenderMessage() {
        return senderMessage;
    }

    public String getReceiverMessage() {
        return receiverMessage;
    }

    public boolean getIsSender() {
        return IsSender;
    }

    public Integer getSeenStateNum() {
        return SeenStateNum;
    }

    public String getMessageType() {
        return MessageType;
    }

    public String getMessageTime(){
        return MessageTime;
    }
}
