package com.telega.bot.telega_bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;

    public UpdateConsumer( ) {
        this.telegramClient = new OkHttpTelegramClient("8663172762:AAEBKxGiY2FUYBsuj-zHsMDyptiLJlYGpZA");
    }


    @Override
    public void consume(Update update) {

        var chatId=update.getMessage().getChatId();

        System.out.printf("Having message:  %s from %s%n",
                update.getMessage().getText(),
                chatId
        );

        SendMessage message = SendMessage.builder()
                .text("Hello there!" + "\n" + "Your message is: " + update.getMessage().getText())
                .chatId(chatId)
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
