package ru.ifmo.practice;

import lombok.val;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendSticker;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Properties;

@Log4j2
public class ChatBot extends TelegramLongPollingBot {
    private String botToken;
    private String botUsername;

    private HashSet<String> users = new HashSet<>();
    private ArrayList<Message> messages = new ArrayList<>();

    @SneakyThrows
    public ChatBot(String pathSettings){
        val props = new Properties();

        try (InputStream inStream = ChatBot.class.getResourceAsStream(pathSettings)) {
            props.load(inStream);

            botToken = props.getProperty("token");
            botUsername = props.getProperty("username");
        }
        catch (Exception e){
            log.fatal("Can't load settings file!", e);
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        // Init bot
        ApiContextInitializer.init();
        val telegramBotsApi = new TelegramBotsApi()
                .registerBot(new ChatBot("/settings.properties"));
    }

    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public void onUpdateReceived(Update update) {
        if (update.getMessage() != null){
            getMessage(update);
        }
    }

    private void getMessage(Update update){
        // save message
        Message message = update.getMessage();
        messages.add(message);

        printMessageToLog(message);

        // save username
        // if user is new
        if (users.add(message.getChat().getUserName())) {
            String first_name = message.getChat().getFirstName();
            sendMessage(message, first_name + ", I haven't time :(\nCan you write to me later?");
        }
        else {
            String[] angryStickerTokens = {
                    "CAADBAADGgIAAuJy2QABeEernu20KnMC",
                    "CAADAgADxgEAAu5izQdODlEtrOY2_gI",
                    "CAADAgADBQUAAmvEygqACJYqPM8VfwI",
                    "CAADAgAD2QEAAu5izQcssIrlHhk7xgI",
                    "CAADAgADFwADwyiDDZxXAAGHyl5myAI",
                    "CAADAgADPgMAAsSraAtfLLQnD0YAAcIC"
            };

            sendSticker(message, angryStickerTokens[new Random().nextInt(angryStickerTokens.length)]);
        }
    }

    @SneakyThrows
    private void sendMessage(Message message, String text) {
        val sMessage = new SendMessage()
            .setChatId(message.getChatId())
            .setText(text);
        //noinspection deprecation
        sendMessage(sMessage);
    }

    @SneakyThrows
    private void sendSticker(Message message, String stickerToken){
        val sticker = new SendSticker()
            .setChatId(message.getChatId())
            .setSticker(stickerToken);

        sendSticker(sticker);
    }

    private void printMessageToLog(Message message){
        log.info("login:" + message.getChat().getUserName());

        if (message.getText() != null) {
            log.info("text: " + message.getText() + "\n");
        }

        if (message.getSticker() != null) {
            log.info("sticker: " + message.getSticker().getFileId() + "\n");
        }
    }
}