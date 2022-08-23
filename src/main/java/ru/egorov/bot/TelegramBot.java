package ru.egorov.bot;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ru.egorov.nasa.NasaStart;

@Getter
@Setter
public class TelegramBot extends TelegramWebhookBot {

    private String webHookPath;
    private String botUsername;
    private String botToken;
    private NasaStart nasaStart;

    private SetWebhook setWebhook;


    @Autowired
    public TelegramBot(SetWebhook setWebhook, NasaStart nasaStart) {

        this.setWebhook = setWebhook;
        this.nasaStart = nasaStart;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        Message message = update.getMessage();
        String chat_id = null;
        String textMessage = null;

        if (message != null && message.hasText()) {
            chat_id = message.getChatId().toString();
            textMessage = update.getMessage().getText();
        }

        switch (textMessage.toLowerCase()) {
            case "nasa":
                try {

                    nasaStart.start();
                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setChatId(chat_id);

                    String url = nasaStart.getImageUrl();

                    //если сегодня выложена не картинка
                    if(!url.toLowerCase().contains("jpg")) {
                        execute(new SendMessage(chat_id, url));
                        break;
                    }

                    //если сегодня выложена картинка
                    sendPhoto.setPhoto(new InputFile().setMedia(url));

                    //ограничение длины описания изображения
                    String caption = nasaStart.getCaption();
                    //мак. длина описания
                    int charCountOfCaption = 1024;

                    if (caption.length() <= charCountOfCaption) {
                        charCountOfCaption = caption.length();
                    }

                    sendPhoto.setCaption(nasaStart.getCaption().substring(0, charCountOfCaption));
                    execute(sendPhoto);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                try {
                    execute(new SendMessage(chat_id,
                            "Привет. Я бот, умею скачивать картинки с сайта NASA. Напиши \"NASA\""));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
        }
        return null;
    }

    @Override
    public String getBotPath() {
        return webHookPath;
    }
}
