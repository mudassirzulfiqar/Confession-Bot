/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import model.CMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import util.MessageUtil;

import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main extends ListenerAdapter {

/*
    public static void main(String[] args) {
        //We construct a builder for a BOT account. If we wanted to use a CLIENT account
        // we would use AccountType.CLIENT
        try {

            // FIXME: 20/02/2021 Added this to CONFIG VAR
            JDA jda = JDABuilder.createDefault("ODE4NTgxOTA5MDUwODg0MTE2.YEaJzw.2ANbi_3-fs6OkI6cRJK0PrTEVYQ") // The token of the account that is logging in.
                    .addEventListeners(new Main())   // An instance of a class that will handle events.
                    .build();
            jda.awaitReady(); // Blocking guarantees that JDA will be completely loaded.


        } catch (LoginException e) {
            //If anything goes wrong in terms of authentication, this is the exception that will represent it
            e.printStackTrace();
        } catch (InterruptedException e) {
            //Due to the fact that awaitReady is a blocking method, one which waits until JDA is fully loaded,
            // the waiting can be interrupted. This is the exception that would fire in that situation.
            //As a note: in this extremely simplified example this will never occur. In fact, this will never occur unless
            // you use awaitReady in a thread that has the possibility of being interrupted (async thread usage and interrupts)
            e.printStackTrace();
        }

    }
*/

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //These are provided with every event in JDA
        JDA jda = event.getJDA();                       //JDA, the core of the api.
        long responseNumber = event.getResponseNumber();//The amount of discord events that JDA has received since the last reconnect.


        //Event specific information
        User author = event.getAuthor();                //The user that sent the message
        Message message = event.getMessage();           //The message that was received.
        MessageChannel channel = event.getChannel();    //This is the MessageChannel that the message was sent to.
        //  This could be a TextChannel, PrivateChannel, or Group!

        String msg = message.getContentDisplay();              //This returns a human readable version of the Message. Similar to
        // what you would see in the client.

        boolean bot = author.isBot();                    //This boolean is useful to determine if the User that
        // sent the Message is a BOT or not!

        if (event.isFromType(ChannelType.TEXT)) {

            if (event.getMember() == null) {
                return;
            }
            if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                if (msg.equals("!ping")) {
                    channel.sendMessage("pong!").queue();
                } else if (msg.equals("!roll")) {
                    //In this case, we have an example showing how to use the flatMap operator for a RestAction. The operator
                    // will provide you with the object that results after you execute your RestAction. As a note, not all RestActions
                    // have object returns and will instead have Void returns. You can still use the flatMap operator to run chain another RestAction!

                    Random rand = ThreadLocalRandom.current();
                    int roll = rand.nextInt(6) + 1; //This results in 1 - 6 (instead of 0 - 5)
                    channel.sendMessage("Your roll: " + roll)
                            .flatMap(
                                    (v) -> roll < 3, // This is called a lambda expression. If you don't know what they are or how they work, try google!
                                    // Send another message if the roll was bad (less than 3)
                                    sentMessage -> channel.sendMessage(" wasn't very good... Must be bad luck!\n")
                            )
                            .queue();
                } else if (msg.startsWith("!setup")) {
                    if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                        event.getGuild()
                                .createTextChannel(SuccessMessage.CHANNEL_CONFESSION_NAME)
                                .setTopic(SuccessMessage.SETUP_SUCCESS_MSG)
                                .queue(this::createConfessChannel);

                    }
                }/* else if (msg.equals("!link channel")) {
                    String CHANNEL_ID = event.getChannel().getId();
                    String serverId = event.getGuild().getId();
                    DatabaseHelper.getInstance().saveChannelId(serverId, CHANNEL_ID);
                    event.getJDA()
                            .getTextChannelById(CHANNEL_ID)
                            .sendMessage(new EmbedBuilder()
                                    .setTitle(NAME_OF_BOT)
                                    .setDescription(CHANNEL_LINK_MSG)
                                    .build())
                            .queue();
                }*/ else {

                }
                /*else if (msg.equals("!link admin")) {
                    List<User> mentionedUsers = message.getMentionedUsers();
                    ADMIN_ID = mentionedUsers.get(0).getIdLong();
                }*/
            }


        } else if (event.isFromType(ChannelType.PRIVATE)) {
            if (msg.equals("!hi")) {
                SuccessMessage.respondWakeCommand(event.getChannel()).queue();
            } else if (msg.startsWith("!c")) {
                //The message was sent in a PrivateChannel.

                MessageUtil messageUtil = new MessageUtil();
                CMessage cMessage = messageUtil.parseMessage(msg);

                if (cMessage.getErrorMsg() != null) {
                    ErrorMessage.wrongPatternError(event.getChannel(), cMessage.getErrorMsg()).queue();
                    return;
                }

                String CHANNEL_ID = cMessage.getChannelId();

                // TODO: 20/02/2021 if no channel found then
                // TODO: 20/02/2021 need to link admin for fall back message
                if (event.getJDA().getTextChannelById(CHANNEL_ID) == null) {
                    ErrorMessage.channelNotExist(event.getChannel());
                    return;
                }

                String imgUrl = "";
                if (!event.getMessage().getAttachments().isEmpty()) {
                    for (Message.Attachment attachment : event.getMessage().getAttachments()) {
                        imgUrl = attachment.getUrl();
                    }
                }

                event.getJDA().getTextChannelById(CHANNEL_ID)
                        .sendMessage(new EmbedBuilder()
                                .setThumbnail(imgUrl)
                                .setTitle(SuccessMessage.NAME_OF_BOT)
                                .setDescription(cMessage.getMessage())
                                .build()).queue();

            } else if (msg.startsWith("!list servers")) {
                if (msg.replace("!list servers", "").isEmpty()) {
                    // its mean that we need to display the list of the guilds this bot is configured
                    String listOfServers = event.getJDA().getGuilds().stream()
                            .map(guild -> guild.getId() + " " + guild.getName()).collect(Collectors.joining("\n"));

                    SuccessMessage.listOfServers(event.getChannel(), listOfServers).queue();

                } else {
                    // extract the menue
                }
                System.out.println(event.getJDA().getGuilds().toString());
            } else if (msg.startsWith(Commands.DM.REACT_MESSAGE_RAW)) {
                if (msg.replace(Commands.DM.REACT_MESSAGE_RAW, "").isEmpty()) {
                    ErrorMessage.wrongPatternErrorReact(event.getChannel()).queue();
                } else {
                    String messageId = msg.split(" ")[2].trim();
                    for (Guild guild : event.getJDA()
                            .getGuilds()) {
                        for (GuildChannel guildChannel : guild.getChannels()) {
                            if (guildChannel != null) {
                                TextChannel textChannelById = guildChannel.getGuild().getTextChannelById(guildChannel.getId());
                                if (textChannelById != null) {
                                    System.out.println(textChannelById.getName());
                                    textChannelById.retrieveMessageById(messageId).queue((success) -> {
                                                System.out.println("YYAY " + success.toString());
                                                success.addReaction(msg.split(" ")[3]).queue(new Consumer<Void>() {
                                                    @Override
                                                    public void accept(Void unused) {
                                                        String latestMessageId = event.getChannel().getLatestMessageId();
                                                        event.getChannel().retrieveMessageById(latestMessageId).queue(new Consumer<Message>() {
                                                            @Override
                                                            public void accept(Message message) {
                                                                message.addReaction(msg.split(" ")[3]).queue();
                                                            }
                                                        });
                                                        // TODO: 11/03/2021 add reaction to message for success
                                                    }
                                                });
                                            },
                                            (exception) ->
                                                    System.out.println("Cannot found"));
                                }
                            }
                        }
                    }


//                    String lastMessageid = event.getJDA().getTextChannelById("818813037905313792").getLatestMessageId();
//                    event.getJDA().getGuilds().get(0).getH
//                    event.getJDA().getTextChannelById("818813037905313792").retrieveMessageById(lastMessageid)
//                            .queue(message1 -> message1.addReaction("\uD83D\uDE02").queue());

                    // extract the menue
                }
            } else if (msg.startsWith("!r")) {
                TextChannel textChannelById = getConfessionChannel(event);
                if (textChannelById == null) return;
                String latestMessageId = textChannelById.getLatestMessageId();
                event.getChannel().retrieveMessageById(latestMessageId).queue(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) {
                        SuccessMessage.sendReactionToLastConfession(message, msg).queue();
                    }
                });
                // TODO: 11/03/2021 add reaction to message for success

            }


        }

    }

    String CHANNEL_ID = "";

    @Nullable
    private TextChannel getConfessionChannel(MessageReceivedEvent event) {
        /*
         * We are using this below technique to save our database transaction cost.
         */
//                if (CHANNEL_ID.isEmpty()) {
        try {
            String queryChannelId = DatabaseHelper.getInstance().queryChannelId();
            String channelId = queryChannelId;
            if (queryChannelId != null) {
                CHANNEL_ID = channelId;
            } else {
                CHANNEL_ID = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//                }
        /*
         * If still Channel Id is empty then we will send the message
         */
        if (CHANNEL_ID.isEmpty()) {
            ErrorMessage.channelNotExist(event.getChannel()).queue();
            return null;
        }

        TextChannel textChannelById = event.getJDA().getTextChannelById(CHANNEL_ID);
        if (textChannelById == null) {
            ErrorMessage.channelNotExist(event.getChannel()).queue();
            return null;
        }
        return textChannelById;
    }


    private void createConfessChannel(TextChannel confessChannel) {
        String CHANNEL_ID = confessChannel.getId();
        String serverId = confessChannel.getGuild().getId();

//        DatabaseHelper.getInstance().saveChannelId(serverId, CHANNEL_ID);

        SuccessMessage.channelCreateMessage(confessChannel).queue();
    }
}


