package com.telega.bot.telega_bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;

@Component
public class MyTelegramBot implements SpringLongPollingBot {

    private final UpdateConsumer updateConsumer;

    public MyTelegramBot(UpdateConsumer updateConsumer){
        this.updateConsumer = updateConsumer;
    }

    @Override
    public String getBotToken() {
        //https://t.me/MaximusPicBot
        return "8663172762:AAEBKxGiY2FUYBsuj-zHsMDyptiLJlYGpZA";
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return updateConsumer;
    }
}
