import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.awt.*;

public class SuccessMessage {

    public static final String NAME_OF_BOT = "CONFESSION";
    public static final String CHANNEL_CONFESSION_NAME = "CONFESSION";
    public static final String CHANNEL_WELCOME_MSG = "This Channel has been created for Confessions \nHow does it work?\n" +
            "If you see me online just write a personal message to me starting with `!c` \n Example:\n " + Commands.DM.SEND_CONFESSION + "\n" +
            "Test me by sending your first personal message " + Commands.DM.HI_BOT;
    public static final String SETUP_SUCCESS_MSG = "Confessional channel created. PM your confession Bot";
    public static final String SIGNATURE = "Developed by *IQ-500*";

    public static MessageAction channelCreateMessage(MessageChannel channel) {
        return channel.sendMessage(new EmbedBuilder()
                .setTitle(NAME_OF_BOT)
                .setDescription(CHANNEL_WELCOME_MSG)
                .setAuthor("The id of this channel is: " + channel.getId())
                .setFooter(SIGNATURE)
                .setColor(Color.blue)
                .build());
    }

    public static MessageAction respondWakeCommand(MessageChannel channel) {
        return channel.sendMessage(new EmbedBuilder()
                .setTitle("Hello! Its " + NAME_OF_BOT)
                .setDescription("Send any message to me starting with following pattern: \n" + Commands.DM.SEND_CONFESSION + "\n" + Commands.DM.SEND_CONFESSION_EXAMPLE)
                .setColor(Color.blue)
                .build());
    }

    public static MessageAction listOfServers(MessageChannel channel, String listOfServers) {
        return channel.sendMessage(new EmbedBuilder()
                .setTitle("List of Servers")
                .setDescription(listOfServers)
                .setColor(Color.blue)
                .build());

    }

    public static RestAction<Void> sendReactionToLastConfession(Message message, String msg) {

        String reaction = msg.replace("!r", "");
        if (reaction.isEmpty()) {
            return null;
        }

        return message.addReaction(reaction);

    }

}
