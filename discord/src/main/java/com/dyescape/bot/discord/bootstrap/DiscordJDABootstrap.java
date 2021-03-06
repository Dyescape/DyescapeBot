package com.dyescape.bot.discord.bootstrap;

import com.dyescape.bot.data.suit.DataSuit;
import com.dyescape.bot.discord.listener.JoinLeaveAnnouncementListener;
import com.dyescape.bot.discord.listener.ModerationListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;

@Configuration
public class DiscordJDABootstrap {

    private static final String TOKEN_PREFIX = "Bot ";

    private final String token;
    private final DataSuit dataSuit;

    @Autowired
    public DiscordJDABootstrap(@Value("${discord.token}") String token, DataSuit dataSuit) {
        this.token = stripOptionalBotPrefix(token);
        this.dataSuit = dataSuit;
    }

    @Bean
    public JDA getJDA() throws LoginException {
        JDABuilder builder = JDABuilder.create(this.token,
                GatewayIntent.GUILD_BANS,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_EMOJIS
        );

        builder.addEventListeners(
                new ModerationListener(this.dataSuit),
                new JoinLeaveAnnouncementListener(this.dataSuit)
        );

        builder.disableCache(
                CacheFlag.ACTIVITY,
                CacheFlag.VOICE_STATE,
                CacheFlag.CLIENT_STATUS
        );

        return builder.build();
    }

    String stripOptionalBotPrefix(String token) {
        if (token.startsWith(TOKEN_PREFIX)) {
            token = token.substring(TOKEN_PREFIX.length());
        }
        return token;
    }
}
