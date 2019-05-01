package com.dyescape.dyescapebot.command.discord;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.JDACommandEvent;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.google.common.base.Strings;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.Color;

import com.dyescape.dyescapebot.moderation.Moderation;
import com.dyescape.dyescapebot.util.TimeUtil;

@CommandAlias("!")
public class ModerationCommand extends BaseCommand {

    // -------------------------------------------- //
    // DEPENDENCIES
    // -------------------------------------------- //

    private final Moderation moderation;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public ModerationCommand(Moderation moderation) {
        this.moderation = moderation;
    }

    // -------------------------------------------- //
    // PUNISHMENT COMMANDS
    // -------------------------------------------- //

    @Subcommand("kick")
    @CommandPermission("moderator")
    @Syntax("<User> [Reason]")
    @Description("Kick a user from the server")
    public void onKickCommand(JDACommandEvent e, Member member, @Optional String reason) {
        this.moderation.kick(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason, handler -> {

            if (handler.succeeded()) {
                e.sendMessage(this.embed(String.format("User %s was kicked.", member.getEffectiveName())));
            } else {
                e.sendMessage(this.embed(String.format("Error: %s", handler.cause().getMessage())));
            }
        });
    }

    @Subcommand("ban")
    @CommandPermission("moderator")
    @Syntax("<User> [Reason]")
    @Description("Permanently ban a user from the server")
    public void onBanCommand(JDACommandEvent e, Member member, @Optional String reason) {
        this.moderation.ban(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason, handler -> {

            if (handler.succeeded()) {
                e.sendMessage(this.embed(String.format("User %s was banned.", member.getEffectiveName())));
            } else {
                e.sendMessage(this.embed(String.format("Error: %s", handler.cause().getMessage())));
            }
        });
    }

    @Subcommand("tempban")
    @CommandPermission("moderator")
    @Syntax("<User> <Time> [Reason]")
    @Description("Temporarily ban a user from the server")
    public void onTempBanCommand(JDACommandEvent e, Member member, String time, @Optional String reason) {
        this.moderation.tempban(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason,
                TimeUtil.parseFromRelativeString(time), handler -> {

            if (handler.succeeded()) {
                long punishmentTime = TimeUtil.parseFromRelativeString(time);
                e.sendMessage(this.embed(String.format("User %s was banned for %s.",
                        member.getEffectiveName(), TimeUtil.parsePunishmentTime(punishmentTime))));
            } else {
                e.sendMessage(this.embed(String.format("Error: %s", handler.cause().getMessage())));
            }
        });
    }

    @Subcommand("unban")
    @CommandPermission("moderator")
    @Syntax("<User>")
    @Description("Unban a user from the server")
    public void onUnbanCommand(JDACommandEvent e, User user) {
        this.moderation.unban(e.getIssuer().getGuild().getIdLong(), user.getIdLong(), handler -> {

            if (handler.succeeded()) {

                e.sendMessage(this.embed(String.format("User %s was unbanned.",
                        user.getName() + user.getDiscriminator())));
            } else {
                e.sendMessage(this.embed(String.format("Error: %s", handler.cause().getMessage())));
            }
        });
    }

    @Subcommand("mute")
    @CommandPermission("moderator")
    @Syntax("<User> [Reason]")
    @Description("Permanently mute a user on the server")
    public void onMuteCommand(JDACommandEvent e, Member member, @Optional String reason) {
        this.moderation.mute(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason, handler -> {

            if (handler.succeeded()) {
                e.sendMessage(this.embed(String.format("User %s was muted.", member.getEffectiveName())));
            } else {
                e.sendMessage(this.embed(String.format("Error: %s", handler.cause().getMessage())));
            }
        });
    }

    @Subcommand("tempmute")
    @CommandPermission("moderator")
    @Syntax("<User> <Time> [Reason]")
    @Description("Temporarily mute a user on the server")
    public void onTempMuteCommand(JDACommandEvent e, Member member, String time, @Optional String reason) {
        this.moderation.tempmute(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason,
                TimeUtil.parseFromRelativeString(time), handler -> {

            if (handler.succeeded()) {

                long punishmentTime = TimeUtil.parseFromRelativeString(time);
                e.sendMessage(this.embed(String.format("User %s was muted for %s.",
                        member.getEffectiveName(), TimeUtil.parsePunishmentTime(punishmentTime))));
            } else {
                e.sendMessage(this.embed(String.format("Error: %s", handler.cause().getMessage())));
            }
        });
    }

    @Subcommand("unmute")
    @CommandPermission("moderator")
    @Syntax("<User>")
    @Description("Unmute a user on the server")
    public void onUnmuteCommand(JDACommandEvent e, User user) {
        this.moderation.unmute(e.getIssuer().getGuild().getIdLong(), user.getIdLong(), handler -> {

            if (handler.succeeded()) {

                e.sendMessage(this.embed(String.format("User %s#%s was unmuted.",
                        user.getName(), user.getDiscriminator())));
            } else {
                e.sendMessage(this.embed(String.format("Error: %s", handler.cause().getMessage())));
            }
        });
    }

    @Subcommand("channelban|banchannel")
    @CommandPermission("moderator")
    @Syntax("<User> <Channel> [reason]")
    @Description("Ban a user from a channel (revokes read & write access)")
    public void onChannelBanCommand(JDACommandEvent e, Member member, TextChannel channel,
                                    @Optional String reason) {

        this.moderation.channelBan(member.getGuild().getIdLong(), member.getUser().getIdLong(),
                channel.getIdLong(), reason, handler -> {
                    if (handler.succeeded()) {
                        e.sendMessage(this.embed(String.format("User %s was banned from channel #%s.",
                                member.getEffectiveName(), channel.getName())));
                    } else {
                        e.sendMessage(this.embed(String.format("Error: %s", handler.cause().getMessage())));
                    }
                });
    }

    @Subcommand("channeltempban|tempbanchannel|tempchannelban")
    @CommandPermission("moderator")
    @Syntax("<User> <Channel> <Time> [Reason]")
    @Description("Temporarily ban a user from a channel (revokes read & write access)")
    public void onChannelTempBanCommand(JDACommandEvent e, Member member, TextChannel channel,
                                        String time, @Optional String reason) {

        this.moderation.channelTempBan(member.getGuild().getIdLong(), member.getUser().getIdLong(),
                channel.getIdLong(), reason, TimeUtil.parseFromRelativeString(time), handler -> {
                    if (handler.succeeded()) {
                        long punishmentTime = TimeUtil.parseFromRelativeString(time);
                        e.sendMessage(this.embed(String.format("User %s was banned from channel #%s for %s.",
                                member.getEffectiveName(), channel.getName(),
                                TimeUtil.parsePunishmentTime(punishmentTime))));
                    } else {
                        e.sendMessage(this.embed(String.format("Error: %s", handler.cause().getMessage())));
                    }
                });
    }

    @Subcommand("channelmute|mutechannel")
    @CommandPermission("moderator")
    @Syntax("<User> <Channel> [Reason]")
    @Description("Mute a user in a channel (revokes write access)")
    public void onChannelMuteCommand(JDACommandEvent e, Member member, TextChannel channel, @Optional String reason) {
        this.moderation.channelMute(member.getGuild().getIdLong(), member.getUser().getIdLong(),
                channel.getIdLong(), reason, handler -> {

                    if (handler.succeeded()) {
                        e.sendMessage(this.embed(String.format("User %s was muted in channel #%s.",
                                member.getEffectiveName(), channel.getName())));
                    } else {
                        e.sendMessage(this.embed(String.format("Error: %s", handler.cause().getMessage())));
                    }
        });
    }

    @Subcommand("channeltempmute|tempmutechannel|tempchannelmute")
    @CommandPermission("moderator")
    @Syntax("<User> <Channel> <Time> [Reason]")
    @Description("Temporarily mute a user in a channel (revokes  write access)")
    public void onChannelTempMuteCommand(JDACommandEvent e, Member member, TextChannel channel,
                                         String time, @Optional String reason) {
        this.moderation.channelTempMute(member.getGuild().getIdLong(), member.getUser().getIdLong(),
                channel.getIdLong(), reason, TimeUtil.parseFromRelativeString(time), handler -> {

            if (handler.succeeded()) {
                long punishmentTime = TimeUtil.parseFromRelativeString(time);
                e.sendMessage(this.embed(String.format("User %s was muted in channel #%s for %s.",
                        member.getEffectiveName(), channel.getName(),
                        TimeUtil.parsePunishmentTime(punishmentTime))));
            } else {
                e.sendMessage(this.embed(String.format("Error: %s", handler.cause().getMessage())));
            }
        });
    }

    @Subcommand("warn")
    @CommandPermission("moderator")
    @Syntax("<User> [Reason]")
    @Description("Warn a user on the server")
    public void onWarnCommand(JDACommandEvent e, Member member, @Optional String reason) {
        this.moderation.warn(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason, handler -> {

            if (handler.succeeded()) {
                e.sendMessage(this.embed(String.format("User %s was warned.", member.getEffectiveName())));
            } else {
                e.sendMessage(this.embed(String.format("Error: %s", handler.cause().getMessage())));
            }
        });
    }

    // -------------------------------------------- //
    // CONFIGURATION COMMANDS
    // -------------------------------------------- //

    @Subcommand("warnaction|warningaction")
    @CommandPermission("moderator")
    @Syntax("<WarningPoints> <Action> [Time]")
    @Description("Configure the moderation actions for reached warning points")
    public void onWarningActionCommand(JDACommandEvent e, Integer warningPoints, String action, @Optional String time) {
        if (Strings.isNullOrEmpty(action)) {

            e.sendMessage(String.format("I am going to set the action of %s warning points to %s, action time: %s",
                    warningPoints, action, time));
        } else {
            e.sendMessage(String.format("I am going to delete the action of %s warning points.",
                    warningPoints));
        }
    }

    @Subcommand("addmod|addmoderator")
    @CommandPermission("administrator")
    @Syntax("<Role>")
    @Description("Mark a role as Moderator role for this bot")
    public void onAddModeratorCommand(JDACommandEvent e, Role role) {

        e.sendMessage(this.embed(String.format("Adding %s to the list of Moderator roles.", role.getName())));
    }

    @Subcommand("removemod|removemoderator")
    @CommandPermission("administrator")
    @Syntax("<Role>")
    @Description("Unmark a role as Moderator role for this bot")
    public void onRemoveModeratorCommand(JDACommandEvent e, Role role) {

        e.sendMessage(this.embed(String.format("Removing %s from the list of Moderator roles.", role.getName())));
    }

    @Subcommand("addadmin|addadministrator")
    @CommandPermission("administrator")
    @Syntax("<Role>")
    @Description("Mark a role as Moderator role for this bot")
    public void onAddAdministratorCommand(JDACommandEvent e, Role role) {

        e.sendMessage(this.embed(String.format("Adding %s to the list of Administrator roles.", role.getName())));
    }

    @Subcommand("removeadmin|removeadministrator")
    @CommandPermission("administrator")
    @Syntax("<Role>")
    @Description("Unmark a role as Moderator role for this bot")
    public void onRemoveAdministratorCommand(JDACommandEvent e, Role role) {

        e.sendMessage(this.embed(String.format("Removing %s from the list of Administrator roles.", role.getName())));
    }

    // -------------------------------------------- //
    // PRIVATE
    // -------------------------------------------- //

    private MessageEmbed embed(String message) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setDescription(message);
        eb.setColor(Color.RED);
        return eb.build();
    }
}
