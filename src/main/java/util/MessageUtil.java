package util;

import model.CMessage;

public class MessageUtil {
    public static String EMPTY = "Sorry! wrong format please follow the pattern";
    public static String NO_CHANNEL = "Sorry! please dont forget to add channelId";
    public static String NO_MESSAGE = "Sorry! please dont forget to add confession message";
    public static String WRONG_PATTERN = "Sorry! please dont forget to add confession message";

    public CMessage parseMessage(String eventMessage) {
        try {
            if (eventMessage.isEmpty()) {
                return new CMessage(null, null, EMPTY);
            }
            String message = "";
            String channelId = "";

            if (!eventMessage.contains(":")) {
                return new CMessage(null, null, NO_CHANNEL);
            }

            String[] split = eventMessage.split(":");
            String afterColon = split[1];

            try {
                channelId = afterColon.split(" ")[0];
            } catch (Exception e) {
                return new CMessage(null, null, NO_CHANNEL);
            }

            try {
                message = afterColon.split(" ")[1];
            } catch (Exception e) {
                return new CMessage(null, null, NO_MESSAGE);
            }

            return new CMessage(message, channelId, null);
        } catch (Exception e) {
            return new CMessage(null, null, WRONG_PATTERN);
        }
    }


}
