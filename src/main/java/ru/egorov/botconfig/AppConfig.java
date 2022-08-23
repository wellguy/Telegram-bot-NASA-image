package ru.egorov.botconfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import ru.egorov.bot.TelegramBot;
import ru.egorov.bot.TelegramBotConfig;
import ru.egorov.nasa.NasaStart;

@Setter
@Getter
@Configuration
public class AppConfig {

    private final TelegramBotConfig botConfig;

    public AppConfig(TelegramBotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(botConfig.getWebHookPath()).build();
    }

    @Bean
    public TelegramBot springWebhookBot(SetWebhook setWebhook, NasaStart nasaStart) {
        TelegramBot bot = new TelegramBot(setWebhook, nasaStart);
        bot.setBotToken(botConfig.getBotToken());
        bot.setBotUsername(botConfig.getUserName());
        bot.setWebHookPath(botConfig.getWebHookPath());
        return bot;
    }
}
