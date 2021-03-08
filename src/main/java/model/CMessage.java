package model;

public class CMessage {
    public CMessage(String message, String channelId, String errorMsg) {
        this.message = message;
        this.channelId = channelId;
        this.errorMsg = errorMsg;
    }

    private String message = null;
    private String channelId = null;
    private String errorMsg = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    @Override
    public String toString() {
        return "CMessage{" +
                "message='" + message + '\'' +
                ", channelId='" + channelId + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
