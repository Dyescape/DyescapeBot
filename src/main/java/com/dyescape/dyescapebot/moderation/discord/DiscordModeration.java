package com.dyescape.dyescapebot.moderation.discord;

import com.google.common.base.Strings;
import net.dv8tion.jda.core.JDA;

import javax.inject.Inject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.dyescape.dyescapebot.moderation.Moderation;
import com.dyescape.dyescapebot.util.TimeUtil;

public class DiscordModeration implements Moderation {

    // -------------------------------------------- //
    // DEPENDENCIES
    // -------------------------------------------- //

    private final JDA jda;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    @Inject
    public DiscordModeration(JDA jda) {
        this.jda = jda;
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void warn(long serverId, long userId, String reason) {
        this.sendPrivateMessage(userId, this.getWarnMessage(
                this.getUsername(userId), this.getServername(serverId), reason));
    }

    @Override
    public void kick(long serverId, long userId, String reason) {
        this.sendPrivateMessage(userId, this.getKickMessage(
                this.getUsername(userId), this.getServername(serverId), reason));
    }

    @Override
    public void mute(long serverId, long userId, String reason) {
        this.sendPrivateMessage(userId, this.getMuteMessage(
                this.getUsername(userId), this.getServername(serverId), reason));
    }

    @Override
    public void tempmute(long serverId, long userId, String reason, LocalDateTime tillDateTime) {
        this.sendPrivateMessage(userId, this.getTempMuteMessage(
                this.getUsername(userId), this.getServername(serverId), reason, tillDateTime));
    }

    @Override
    public void ban(long serverId, long userId, String reason) {
        this.sendPrivateMessage(userId, this.getBanMessage(
                this.getUsername(userId), this.getServername(serverId), reason));
    }

    @Override
    public void tempban(long serverId, long userId, String reason, LocalDateTime tillDateTime) {
        this.sendPrivateMessage(userId, this.getTempBanMessage(
                this.getUsername(userId), this.getServername(serverId), reason, tillDateTime));
    }

    // -------------------------------------------- //
    // PRIVATE
    // -------------------------------------------- //

    private void sendPrivateMessage(long userId, String message) {

        // TODO: Check if the user has PMs disabled
        this.jda.getUserById(userId).openPrivateChannel().queue((channel) -> {
            channel.sendMessage(message).queue();
        });
    }

    private String getUsername(Long userId) {
        return this.jda.getUserById(userId).getName();
    }

    private String getServername(Long serverId) {
        return this.jda.getGuildById(serverId).getName();
    }

    private String getWarnMessage(String username, String servername, String reason) {
        StringBuilder builder = this.getStringBuilder(username);

        builder.append(String.format("You have been warned on %s.\n", servername));
        if (!Strings.isNullOrEmpty(reason)) {
            builder.append(String.format("**Reason: **%s\n", reason));
        }

        // TODO: Inform user about how many warnings he/she has, and inform them about
        //      automated actions taken when received a certain amount of points.

        builder.append("\n");
        builder.append("Please respect the rules and guidelines.");

        return builder.toString();
    }

    private String getKickMessage(String username, String servername, String reason) {
        StringBuilder builder = this.getStringBuilder(username);

        builder.append(String.format("You have been kicked from %s.\n", servername));
        if (!Strings.isNullOrEmpty(reason)) {
            builder.append(String.format("**Reason: **%s\n", reason));
        }

        builder.append("\n");
        builder.append("Please respect the rules and guidelines.");

        return builder.toString();
    }

    private String getMuteMessage(String username, String servername, String reason) {
        StringBuilder builder = this.getStringBuilder(username);

        builder.append(String.format("You have been muted on %s.\n", servername));
        if (!Strings.isNullOrEmpty(reason)) {
            builder.append(String.format("**Reason: **%s\n", reason));
        }

        builder.append("\n");
        builder.append("Please respect the rules and guidelines.");

        return builder.toString();
    }

    private String getTempMuteMessage(String username, String servername, String reason, LocalDateTime endTime) {
        StringBuilder builder = this.getStringBuilder(username);

        long banTime = endTime.toInstant(ZoneOffset.UTC).toEpochMilli() - System.currentTimeMillis();

        builder.append(String.format("You have been muted on %s for %s.\n",
                servername, TimeUtil.parsePunishmentTime(banTime)));
        if (!Strings.isNullOrEmpty(reason)) {
            builder.append(String.format("**Reason: **%s\n", reason));
        }

        builder.append("\n");
        builder.append("Please respect the rules and guidelines.");

        return builder.toString();
    }

    private String getBanMessage(String username, String servername, String reason) {
        StringBuilder builder = this.getStringBuilder(username);

        builder.append(String.format("You have been banned from %s.\n", servername));
        if (!Strings.isNullOrEmpty(reason)) {
            builder.append(String.format("**Reason: **%s\n", reason));
        }

        builder.append("\n");
        builder.append("Please respect the rules and guidelines.");

        return builder.toString();
    }

    private String getTempBanMessage(String username, String servername, String reason, LocalDateTime endTime) {
        StringBuilder builder = this.getStringBuilder(username);

        long banTime = endTime.toInstant(ZoneOffset.UTC).toEpochMilli() - System.currentTimeMillis();

        builder.append(String.format("You have been temporarily banned from %s for %s.\n",
                servername, TimeUtil.parsePunishmentTime(banTime)));
        if (!Strings.isNullOrEmpty(reason)) {
            builder.append(String.format("**Reason: **%s\n", reason));
        }

        builder.append("\n");
        builder.append("Please respect the rules and guidelines.");

        return builder.toString();
    }

    private StringBuilder getStringBuilder(String username) {
        return new StringBuilder(String.format("Dear %s,\n\n", username));
    }
}