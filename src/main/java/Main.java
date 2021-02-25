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

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.JDAImpl;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends ListenerAdapter {

    public static final String NAME_OF_BOT = "Pupho";
    // FIXME: 20/02/2021 Need to figure this out
    private static String CHANNEL_ID = "";
    private long ADMIN_ID = 0;


    /**
     * This is the method where the program starts.
     */
    public static void main(String[] args) {
        //We construct a builder for a BOT account. If we wanted to use a CLIENT account
        // we would use AccountType.CLIENT
        try {
            // FIXME: 20/02/2021 Added this to CONFIG VAR
            JDA jda = JDABuilder.createDefault("ODA5NDg1NzM5MDYyNTkxNTEw.YCVyVw.JeOdbC2b0olwbuVxWvVywZhpdXQ") // The token of the account that is logging in.
                    .addEventListeners(new Main())   // An instance of a class that will handle events.
                    .build();
            jda.awaitReady(); // Blocking guarantees that JDA will be completely loaded.
            System.out.println("Finished Building JDA!");


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

        DatabaseHelper.getInstance().createStorage();


    }

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
                        event
                                .getGuild()
                                .createTextChannel(NAME_OF_BOT)
                                .setTopic("Confessional channel created. PM your confession Bot")
                                .queue(this::sendSetupMessage);

                    }
                } else if (msg.equals("!link channel")) {
                    CHANNEL_ID = event.getChannel().getId();
                    DatabaseHelper.getInstance().saveChannelId(CHANNEL_ID);
                    event.getJDA()
                            .getTextChannelById(CHANNEL_ID)
                            .sendMessage(new EmbedBuilder()
                                    .setTitle(NAME_OF_BOT)
                                    .setDescription("This Channel has been linked for Confession \nNote: Only one channel can be configured for Confessions\nUse `!link channel` in any channel to configure it. ")
                                    .build())
                            .queue();
                } else {

                }
                /*else if (msg.equals("!link admin")) {
                    List<User> mentionedUsers = message.getMentionedUsers();
                    ADMIN_ID = mentionedUsers.get(0).getIdLong();
                }*/
            }


        } else if (event.isFromType(ChannelType.PRIVATE)) {
            if (msg.equals("!hi")) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Hello! Its confession bot")
                        .setDescription("Send any message to me starting with `!c` will be send to the configured server \n For example:\n `!c <Write your first confession>`")
                        .setColor(Color.blue)
                        .build()).queue();
            } else if (msg.startsWith("!c")) {
                //The message was sent in a PrivateChannel.
                //In this example we don't directly use the privateChannel, however, be sure, there are uses for it!
                PrivateChannel privateChannel = event.getPrivateChannel();
                if (CHANNEL_ID.isEmpty()) {
                    try {
                        if (DatabaseHelper.getInstance().queryChannelId() != null) {
                            CHANNEL_ID = DatabaseHelper.getInstance().queryChannelId();
                        } else {
                            CHANNEL_ID = "";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                /**
                 * If still Channel Id is empty then we will send the message
                 */
                if (CHANNEL_ID.isEmpty()) {
                    event.getChannel().sendMessage(
                            new EmbedBuilder()
                                    .setTitle("Error!!!")
                                    .setDescription("I cannot find the configured channel for Bot. Come back later")
                                    .setColor(Color.RED).build()).queue();

                }


                // TODO: 20/02/2021 if no channel found then
                // TODO: 20/02/2021 need to link admin for fall back message
                if (event.getJDA().getTextChannelById(CHANNEL_ID) == null) {
                    event.getChannel().sendMessage(
                            new EmbedBuilder()
                                    .setTitle("Error!!!")
                                    .setDescription("I cannot find the configured channel for Bot. Come back later")
                                    .setColor(Color.RED).build()).queue();
                } else {
                    event.getJDA().getTextChannelById(CHANNEL_ID)
                            .sendMessage(new EmbedBuilder()
                                    .setTitle(NAME_OF_BOT)
                                    .setDescription(msg.replace("!c", ""))
                                    .build()).queue();
                }
            }

        }

    }

    private void sendSetupMessage(TextChannel confessChannel) {
        CHANNEL_ID = confessChannel.getId();
        DatabaseHelper.getInstance().saveChannelId(CHANNEL_ID);
        confessChannel.sendMessage(new EmbedBuilder()
                .setTitle(NAME_OF_BOT)
                .setDescription("This Channel has been linked for Confession \nNote: Only one channel can be configured for Confessions\nUse `!link channel` in any channel to configure it for incoming Confession.")
                .setColor(Color.blue)
                .build()).queue(message -> {
            confessChannel.sendMessage(new EmbedBuilder()
                    .setTitle("How does it work?")
                    .setDescription("If you see me online just write a personal message to me starting with `!c` \n Example:\n `!c <Write your first confession>` \n DM command :`!hi`")
                    .setFooter("Developed by **moodi**")
                    .build()).queue();
        });
    }
}


