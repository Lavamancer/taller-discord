package com.lavamancer.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BotComponent extends ListenerAdapter {

    private static final String DISCORD_TOKEN = "1234";
    private static final long BOT_TESTING_CHANNEL_ID = 1234L;
    private static final long WARNING_ROLE_ID = 1234L;
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final Pattern regex = Pattern.compile("(!user) (?<userId>[0-9]+)");
    private JDA jda;

    @Autowired RetrofitTool retrofitTool;


    @PostConstruct
    public void init() throws LoginException {
        jda = new JDABuilder(DISCORD_TOKEN).build();
        jda.addEventListener(this);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 12, initialDelay = 1000 * 60 * 60 * 12)
    public void scheduled() {
        TextChannel textChannel = jda.getTextChannelById(BOT_TESTING_CHANNEL_ID);
        Role role = jda.getRoleById(WARNING_ROLE_ID);

        String timestamp = ZonedDateTime.now().format(DATE_TIME_FORMAT);
        if (textChannel != null && role != null) {
            textChannel.sendMessage(timestamp + "\nToca mención a todos los que teneis Warning: " + role.getAsMention()).queue();
        }
    }

    @Scheduled(cron = "0 58 22 * * ?") // Todos los días a las 22:58 lanzará el mensaje
    public void cron() {
        TextChannel textChannel = jda.getTextChannelById(BOT_TESTING_CHANNEL_ID);
        if (textChannel != null) {
            textChannel.sendMessage("Cron message").queue();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        if (message.startsWith("!") && event.getTextChannel().getIdLong() == BOT_TESTING_CHANNEL_ID) {

            if (message.equals("!ping")) {
                event.getTextChannel().sendMessage("hello " + event.getAuthor().getAsMention()).queue();
            }

            if (message.equals("!embed")) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Hello Embed");
                eb.setDescription("Esto es una prueba de un mensaje embebido");
                eb.setColor(Color.WHITE);
                eb.setFooter("Este es el footer");
                eb.setAuthor("Lavamancer");
                eb.setThumbnail("https://s1.eestatic.com/2017/11/10/actualidad/Actualidad_260988845_130057972_1706x960.jpg");
                event.getTextChannel().sendMessage(eb.build()).queue();
            }

            // TODO: Se necesita que el bot tenga permisos
//            if (message.equals("!kick") && event.getMember() != null) {
//                event.getGuild().kick(event.getMember()).queue();
//            }

            Matcher matcher = regex.matcher(message);
            if (matcher.matches()) {
                long userId = Long.parseLong(matcher.group("userId"));
                User user = retrofitTool.getUser(userId);
                String response = "";
                if (user != null) {
                    response = "El nombre de usuario es: " + user.getName();
                } else {
                    response = "Usuario no existe";
                }
                event.getTextChannel().sendMessage(response).queue();
            }

        }

    }



}
