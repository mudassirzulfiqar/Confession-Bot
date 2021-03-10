import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.awt.*;

public class ErrorMessage {
    public static MessageAction wrongPatternError(MessageChannel channel, String error) {
        return showError(channel, "Wrong Command", error + "\n" + Commands.DM.SEND_CONFESSION);
    }

    public static MessageAction channelNotExist(MessageChannel channel) {
        return showError(channel, "Channel", "Im unable to find channel with this Id");
    }

    private static MessageAction showError(MessageChannel channel, String title, String error) {
        return channel.sendMessage(
                new EmbedBuilder()
                        .setTitle(title)
                        .setDescription(error)
                        .setColor(Color.RED).build());
    }

    public static MessageAction wrongPatternErrorReact(MessageChannel channel) {
        return showError(channel, "Wrong Command", "Please insert the message id to react" + "\n" + Commands.DM.REACT_MESSAGE);
    }
}
