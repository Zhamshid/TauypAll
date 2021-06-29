package kz.app.taýypall.model;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private String postName;

    public Chat(String sender, String receiver, String message, String postName){
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.postName = postName;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }
}
