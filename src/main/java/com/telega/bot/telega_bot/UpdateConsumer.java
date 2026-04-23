package com.telega.bot.telega_bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;

    public UpdateConsumer() {
        this.telegramClient = new OkHttpTelegramClient("");
    }


    @Override
    public void consume(Update update) {

        if (update.hasMessage()) {
            String messageTxt = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (messageTxt.equals("/start")) {
                sendMainMenu(chatId);
            } else if (messageTxt.equals("/keys")) {
                sendReplyKeyboard(chatId);
            }             else if (messageTxt.equals("Hello")) {
                sentMyName(chatId, update.getMessage().getFrom());
            }             else if (messageTxt.equals(".image")) {
                sentImage(chatId);
            } else {
                SendMessage message = SendMessage.builder()
                        .text("My no undustood")
                        .chatId(chatId)
                        .build();
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }

    }

    private void sendReplyKeyboard(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text("keyboard")
                .build();


        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow("Hello", ".image");
        keyboardRows.add(row1);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(keyboardRows);
        markup.setResizeKeyboard(true);

        message.setReplyMarkup(markup);
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        var data = callbackQuery.getData();
        var chatId = callbackQuery.getFrom().getId();
        var userName = callbackQuery.getFrom().getUserName();
        User user = callbackQuery.getFrom();

        switch (data) {
            case "my_name" -> sentMyName(chatId, user);
            case "random" -> sentRandom(chatId);
            case "long_process" -> sentImage(chatId);
            default -> sentMessage(chatId, "unknown action");
        }
    }

    private void sentMessage(Long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .text(text)
                .chatId(chatId)
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sentImage(Long chatId) {
        sentMessage(chatId, "Sending image...");
        new Thread(() -> {
            var imageUrl = "https://picsum.photos/800";
            try {
                URL url = new URL(imageUrl);
                var inputStream = url.openStream();

                SendPhoto sendPhoto = SendPhoto.builder()
                        .chatId(chatId)
                        .photo(new InputFile(inputStream, "rand.jpg"))
                        .caption("Random pic.")
                        .build();

                telegramClient.execute(sendPhoto);

            } catch (TelegramApiException | IOException e) {
                throw new RuntimeException(e);
            }

        }).start();

    }

    private void sentRandom(Long chatId) {
        var random = ThreadLocalRandom.current().nextInt();
        sentMessage(chatId, "sending random number ... \n" + random);

    }

    private void sentMyName(Long chatId, String user) {
        sentMessage(chatId, String.format("Your name is: %s", user));
    }

    private void sentMyName(Long chatId, User user) {

        var text = "Hi your name is %s \n nikname: @%s"
                .formatted(
                        user.getFirstName() + " " + user.getLastName(),
                        user.getUserName()
                );
        sentMessage(chatId, text);
    }


    private void sendMainMenu(long chatId) {
        SendMessage message = SendMessage.builder()
                .text("Welcome, select action")
                .chatId(chatId)
                .build();

        var button1 = InlineKeyboardButton.builder()
                .text("what is the name?")
                .callbackData("my_name")
                .build();

        var button2 = InlineKeyboardButton.builder()
                .text("Random number")
                .callbackData("random")
                .build();

        var button3 = InlineKeyboardButton.builder()
                .text("long query")
                .callbackData("long_process")
                .build();

        List<InlineKeyboardRow> keyboardRows = List.of(
                new InlineKeyboardRow(button1),
                new InlineKeyboardRow(button2),
                new InlineKeyboardRow(button3)
        );


        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboardRows);

        message.setReplyMarkup(markup);

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
